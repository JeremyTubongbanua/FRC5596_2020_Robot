package frc.robot.constants;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class RobotMap{

    //TODO: Change addresses

    public static final int DRIVE_LEFT_MOTOR_MASTER_ADDRESS         = 10;
    public static final int DRIVE_LEFT_MOTOR_SLAVE_ADDRESS          = 11;

    public static final int DRIVE_RIGHT_MOTOR_MASTER_ADDRESS        = 15;
    public static final int DRIVE_RIGHT_MOTOR_SLAVE_ADDRESS         = 16;


    public static final int DRIVE_LEFT_ENCODER_A                    = 6;
    public static final int DRIVE_LEFT_ENCODER_B                    = 7;

    public static final int DRIVE_RIGHT_ENCODER_A                   = 4;
    public static final int DRIVE_RIGHT_ENCODER_B                   = 5;

    public static final int DRIVE_PIGEON_IMU_ADDRESS                = 6;

    public static final SerialPort.Port LIDAR_PORT = Port.kOnboard;

}