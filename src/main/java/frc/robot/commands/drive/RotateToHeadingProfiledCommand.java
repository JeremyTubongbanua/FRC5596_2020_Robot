package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.State;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.constants.RobotConst.DriveConst;
import frc.robot.constants.RobotConst.DriveConst.CharacterizationConst;
import frc.robot.constants.RobotConst.PIDConst;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DriveSubsystem.DrivePower;
import frc.robot.subsystems.DriveSubsystem.DriveVoltage;

public class RotateToHeadingProfiledCommand extends CommandBase {

    private final DriveSubsystem s_drive;
    private final DifferentialDriveKinematics m_kinematics;
    private final SimpleMotorFeedforward ff_drive;
    private final double TIME_PERIOD = 0.02;

    private final ProfiledPIDController pid_turn;
    private double kP = PIDConst.DRIVE_TURN_KP;
    private double kI = PIDConst.DRIVE_TURN_KI;
    private double kD = PIDConst.DRIVE_TURN_KD;

    private double maxVoltage = 10;

    private TrapezoidProfile.Constraints constraints;

    private DifferentialDriveWheelSpeeds previousWheelSpeeds = new DifferentialDriveWheelSpeeds();

    public RotateToHeadingProfiledCommand(DriveSubsystem drive, double headingGoal) {
        super();

        this.s_drive = drive;

        m_kinematics = drive.getKinematics();

        ff_drive = new SimpleMotorFeedforward(CharacterizationConst.KS_VOLTS,
                CharacterizationConst.KV_VOLT_SECONDS_PER_METER,
                CharacterizationConst.KA_VOLT_SECONDS_SQUARED_PER_METER);

        constraints = new Constraints(CharacterizationConst.K_MAX_TURN_DEG_PER_SECOND,
                CharacterizationConst.K_MAX_TURN_ACCEL_DEG_PER_SECOND_SQUARED);

        pid_turn = new ProfiledPIDController(kP, kI, kD, constraints);

        getController().enableContinuousInput(0, 360);
        getController().setTolerance(PIDConst.DRIVE_TURN_TOLERANCE_DEG, PIDConst.DRIVE_TURN_TOLERANCE_DEG_PER_SECOND);

        SendableRegistry.add(pid_turn, "[Drive] - Command", "Rotate To Heading Profiled PID");

        // SendableRegistry.addLW(getController(), "Rotate to Heading Profiled PID");
    }

    public double getCurrentAngle() {
        return s_drive.getPigeonHeading();
    }

    @Override
    public boolean isFinished() {
        return getController().atGoal();
    }

    public void setGoal(double gyroAngle) {
        getController().setGoal(gyroAngle);
    }

    public TrapezoidProfile.State getGoal() {
        return getController().getGoal();
    }

    public TrapezoidProfile.State getSetpoint() {
        return getController().getSetpoint();
    }

    private DrivePower arcadeToPower(double xSpeed, double zRotation) {
        xSpeed = MathUtil.clamp(xSpeed, -1.0, 1.0);

        zRotation = MathUtil.clamp(zRotation, -1.0, 1.0);

        double leftMotorOutput;
        double rightMotorOutput;

        double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

        if (xSpeed >= 0.0) {
            // First quadrant, else second quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            } else {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            } else {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            }
        }

        double leftPower = MathUtil.clamp(leftMotorOutput, -1.0, 1.0);
        double rightPower = MathUtil.clamp(rightMotorOutput, -1.0, 1.0);
        return (new DrivePower(leftPower, rightPower));
    }

    private DriveVoltage powerToVoltage(DrivePower power) {
        double leftVoltage = power.leftPower * maxVoltage;
        double rightVoltage = power.rightPower * maxVoltage;
        return (new DriveVoltage(leftVoltage, rightVoltage));
    }

    private DriveVoltage calculateFeedForwardVoltage(DifferentialDriveWheelSpeeds wheelSpeeds) {

        double leftAcceleration = (wheelSpeeds.leftMetersPerSecond - previousWheelSpeeds.leftMetersPerSecond)
                * TIME_PERIOD;
        double rightAcceleration = (wheelSpeeds.rightMetersPerSecond - previousWheelSpeeds.rightMetersPerSecond)
                * TIME_PERIOD;

        double leftFFVoltage = ff_drive.calculate(wheelSpeeds.leftMetersPerSecond, leftAcceleration);
        double rightFFVoltage = ff_drive.calculate(wheelSpeeds.rightMetersPerSecond, rightAcceleration);

        return (new DriveVoltage(leftFFVoltage, rightFFVoltage));
    }

    @Override
    public void execute() {
        double currentAngle = getCurrentAngle();

        double turnPower = pid_turn.calculate(currentAngle);

        State turnSetpoint = getSetpoint();

        DifferentialDriveWheelSpeeds wheelSpeeds = m_kinematics
                .toWheelSpeeds(new ChassisSpeeds(0, 0, Units.degreesToRadians(turnSetpoint.velocity)));

        // Changes the straight and turn power to left and right voltages
        DriveVoltage pidVoltage = powerToVoltage(arcadeToPower(0, turnPower));

        // Calculates the required feed forward voltages for the left and right wheel
        // speed velocities
        DriveVoltage ffVoltage = calculateFeedForwardVoltage(wheelSpeeds);

        // Adds the pid and feed forward voltages together and clamps them
        DriveVoltage totalVoltage = DriveVoltage.addVoltage(pidVoltage, ffVoltage);
        totalVoltage = DriveVoltage.clampVoltage(totalVoltage, maxVoltage);

        // Sets the voltage
        s_drive.setVoltage(totalVoltage.leftVoltage, totalVoltage.rightVoltage);

        previousWheelSpeeds = wheelSpeeds;

        // SmartDashboard.putData(getController());
        updateSDashboard();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        super.initSendable(builder);
        builder.setSmartDashboardType("Rotate to Heading Profiled Command");
        builder.addDoubleProperty("Gyro Goal", () -> {
            return this.getGoal().position;
        }, null);
    }

    @Override
    public void initialize() {
        super.initialize();
        var controller = getController();
        SmartDashboard.putNumber("kP", controller.getP());
        SmartDashboard.putNumber("kI", controller.getI());
        SmartDashboard.putNumber("kD", controller.getD());
        s_drive.setDeadband(0);
    }

    private void updateSDashboard() {
        var controller = getController();

        double kp = SmartDashboard.getNumber("kP", 0);
        double ki = SmartDashboard.getNumber("kI", 0);
        double kd = SmartDashboard.getNumber("kD", 0);

        controller.setP(kp);
        controller.setI(ki);
        controller.setD(kd);

    }

    private ProfiledPIDController getController() {
        return this.pid_turn;
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        s_drive.setDeadband(DriveConst.DRIVE_THORTTLE_TRIGGER_VALUE);
        SendableRegistry.remove(pid_turn);
    }

}
