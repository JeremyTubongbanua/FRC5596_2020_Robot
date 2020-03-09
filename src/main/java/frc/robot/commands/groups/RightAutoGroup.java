package frc.robot.commands.groups;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.drive.DriveFowardCommand;
import frc.robot.commands.drive.RotateToHeadingProfiledCommand;
import frc.robot.commands.drive.RotateToVisionTargetCommand;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RightAutoGroup extends SequentialCommandGroup{

    private final DriveSubsystem s_drive;
    private final IntakeSubsystem s_intake;
    private final ShooterSubsystem s_shooter;
    private final CameraSubsystem s_camera;

    public RightAutoGroup(DriveSubsystem drive, IntakeSubsystem intake, ShooterSubsystem shooter, CameraSubsystem camera){
        this.s_drive = drive;
        this.s_intake = intake;
        this.s_shooter = shooter;
        this.s_camera = camera;
        addCommands(
            new RotateToHeadingProfiledCommand(s_drive, 30),

            // new RotateToVisionTargetCommand(s_camera, s_drive){
            //     public boolean isFinished(){
            //         if(totalCycles <= CYCLES_UNTIL_CHECK_FINISHED){
            //             return getController().atSetpoint();
            //         }
            //         return false;
            //     }
            // },

            // new ShootBallsCommand(s_intake, s_shooter),
            // new WaitCommand(0.3), 

            new AlignAndShootGroup(s_drive, s_intake, s_shooter, s_camera),

            new RotateToHeadingProfiledCommand(s_drive, 180),
            new DriveFowardCommand(s_drive, 0.4, 1)
            
        );

    }
}