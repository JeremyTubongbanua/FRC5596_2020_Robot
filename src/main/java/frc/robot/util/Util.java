package frc.robot.util;

public class Util{

    //Allows for input values less than trigger values. 
    //Piecewise function, if less than deadzone value, 0        
    //Else linear
    //https://www.desmos.com/calculator/h7gnlk21iz 

    public static double getDeadzoneResult(double controlInput, double deadzoneAmount){

        if(Math.abs(controlInput) < deadzoneAmount){
            controlInput = 0;
        } else {
            if(controlInput>0){
                controlInput = ((controlInput - deadzoneAmount)/(1-deadzoneAmount));
            }else{
                controlInput = ((controlInput + deadzoneAmount)/(1-deadzoneAmount));
            }  
        }
        return controlInput;
    }


    //Ensures motor power values are between -1 and 1
    public static double getMotorLimits(double motorPower){
        if (motorPower < -1){
            motorPower = -1;
        }else if(motorPower > 1){
            motorPower = 1;
        }

        return motorPower;
    }


   public static double setDeadzoneLimits(double controlInput, double deadzoneValue){
        
        controlInput = getDeadzoneResult(controlInput, deadzoneValue);
        controlInput = getMotorLimits(controlInput);
        return controlInput;

    }

    // Ensures that a doulbe is between 0 and 1
    // If less than 0, will be set to 0
    // If greater than 1, will be set to one
    public static double zeroToOne(double num){
        if(num < 0){
            num = 0;
        }else if (num > 1){
            num = 1;
        }
        return num;
    }

    /**
     * Wraps error around, returning the shortest path for a continuous error.
     */
    public static double getContinuousError(double error, double inputRange) {
        if (inputRange > 0) {
          error %= inputRange;
          if (Math.abs(error) > inputRange / 2) {
            if (error > 0) {
              return error - inputRange;
            } else {
              return error + inputRange;
            }
          }
        }
        return error;
      }

}