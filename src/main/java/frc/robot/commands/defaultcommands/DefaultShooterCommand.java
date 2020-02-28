package frc.robot.commands.defaultcommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.constants.RobotConst;
import frc.robot.oi.OperatorController;
import frc.robot.subsystems.ShooterSubsystem;

public class DefaultShooterCommand extends CommandBase {

    private ShooterSubsystem s_shooter;
    private OperatorController oc;

    public DefaultShooterCommand(ShooterSubsystem subsystem) {
        s_shooter = subsystem;
        addRequirements(subsystem);
        oc = RobotContainer.getOperatorController();
    }

    @Override
    public void execute() {
        double rev = oc.getRightTrigger();
        if(Math.abs(rev) > 0.15) {
            s_shooter.setFlywheelSpeed(rev);
        } else {
            s_shooter.setFlywheelSpeed(0);
        }

        double hood = oc.getRightStickY();
        if(Math.abs(hood) > 0.15) {
            s_shooter.setHoodSpeed(hood*0.2);
        } else {
            s_shooter.setHoodSpeed(0);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}