package frc.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.constants.JoystickMap;
import frc.robot.constants.RobotConst;

/**
 * Controller Map:
 * 
 * Left Stick X:
 * Left Stick Y: 
 * Left Stick Button:
 * 
 * Right Stick X: 
 * Right Stick Y: Shooter Hood Rotation UP=CLOSE DOWN=OPEN
 * Right Stick Button: 
 * 
 * Left Bumper: Manual Outake through BOTTOM
 * Right Bumper: Fly Wheel Run
 * 
 * Left Trigger: 
 * Right Trigger: Dumby-Proof Intake
 * 
 * Button A:
 * Button B: Reset IntakeSubsystem Logic
 * Button X: 
 * Button Y: 
 * 
 * Button Select:
 * Button Start:
 * 
 * POV 0: (POV Up) Outake balls from bottom
 * POV 45:
 * POV 90: 
 * POV 135:
 * POV 180: (POV Down) Run all intake motors to spit out from top.
 * POV 225:
 * POV 270:
 */
public class OperatorController extends Controller {

    private Joystick operator;

    public OperatorController(int port) {
        super(port);
        operator = super.getJoystick();
    }

    /**
     * @return true if left trigger is pressed down passed at least RobotConst.ControllerConst.DEADZONE_INTAKE
     */
    public boolean isHoldingLeftTrigger() {
        return operator.getRawAxis(JoystickMap.LEFT_TRIGGER) > RobotConst.ControllerConst.DEADZONE_TRIGGER;
    }

    /**
     * @return true if right trigger is pressed down passed at least RobotConst.ControllerConst.DEADZONE_INTAKE
     */
    public boolean isHoldingRightTrigger() {
        return operator.getRawAxis(JoystickMap.RIGHT_TRIGGER) > RobotConst.ControllerConst.DEADZONE_TRIGGER;
    }

    /**
     * @return true if B is pressed
     */
    public boolean getAutoShootButton() {
        return operator.getRawButton(JoystickMap.BUTTON_B);
    }

    /**
     * @return true if pressing POV North
     */
    public boolean isPOVUp() {
        return operator.getPOV() == JoystickMap.POV_NORTH;
    }

    /**
     * @return true if pressing POV South
     */
    public boolean isPOVDown() {
        return operator.getPOV() == JoystickMap.POV_SOUTH;
    }

    public boolean isFlyWheelRun() {
        return operator.getRawButton(JoystickMap.BUTTON_RIGHT_BUMPER);
    }

    public double getHoodRotation() {
        return operator.getRawAxis(JoystickMap.RIGHT_STICK_Y);
    }

    public boolean isOutaking() {
        return operator.getRawButton(JoystickMap.BUTTON_LEFT_BUMPER);
    }

}