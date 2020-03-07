package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.constants.RobotConst.IntakeConst;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeSubsystem.Position;
import frc.robot.subsystems.IntakeSubsystem.Ball;


public class MoveBallsToShootCommand extends CommandBase {

    private IntakeSubsystem s_intake;
    private boolean flywheelReady = false;
    private boolean previous = false;

    public MoveBallsToShootCommand(IntakeSubsystem subsystem) {
        super();
        s_intake = subsystem;
        addRequirements(subsystem);
        s_intake.initAuto();

    }

    @Override
    public void execute() {
        if (!s_intake.isSensorFiveActivated()) {
            // No ball in top position
            s_intake.setSpeeds(IntakeConst.ENTRY_SPEED, IntakeConst.CURVE_SPEED, IntakeConst.LOWER_VERTICAL_SPEED,
                    IntakeConst.UPPER_VERTICAL_SPEED);
            flywheelReady = false;    
            // if(previous){
            //     List<Ball> mag = s_intake.mag;
            //     if(mag.size() > 1) {
            //         mag.remove(0);
            //         s_intake.moveBallsOneStage();
            //     }
            //     previous = false;
            // } 
            // s_intake.moveBallsOneStage();   
        } else {
            previous = true;
            // Ball in top position,
            if (flywheelReady) {
                // // Flywheel ready, shoot balls
                // s_intake.setSpeeds(IntakeConst.ENTRY_SPEED, IntakeConst.CURVE_SPEED, IntakeConst.LOWER_VERTICAL_SPEED,
                //         0.8);
                s_intake.setSpeeds(0, 0, 0, 0.9);
            } else {
                s_intake.setSpeeds(0, 0, 0, 0);
            }
        }
    }

    public void setFlywheelReady(boolean ready) {
        this.flywheelReady = ready;
    }

    @Override
    public boolean isFinished() {
        System.out.println("NUM OF BALLS: " + s_intake.getAmountOfBalls());
        return (s_intake.getAmountOfBalls() == 0);
        // return false;
    }

    @Override
    public void end(boolean interrupted) {
        s_intake.setSpeeds(0, 0, 0, 0);
    }
}