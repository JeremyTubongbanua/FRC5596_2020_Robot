package frc.robot.util;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

public class UARTLidar {

    // Datasheet:
    // https://www.robotshop.com/media/files/pdf2/rb-ben-03_2017-11-23-benewake-tfmini-micro-lidar-module-ip65-12-m-datasheet.pdf

    private final int BAUD_RATE = 115200;
    private final int DATA_BITS = 8;
    private final Parity PARITY_BITS = Parity.kNone;
    private final StopBits STOP_BITS = StopBits.kOne;

    private final int NUM_OF_BYTES = 9;

    private final byte FRAME_HEADER = (byte) 0x59;

    /**
     * The last lidar measurement.
     */
    private int lastMeasurement;

    private final SerialPort lidar;

    public UARTLidar(Port serialPort) {
        lidar = new SerialPort(BAUD_RATE, serialPort, DATA_BITS, PARITY_BITS, STOP_BITS);
        lidar.setReadBufferSize(NUM_OF_BYTES);
    }

    /**
     * Must be called periodicly in the main control loop. Should be called before
     * {@link #getDistanceCM()} but SHOULD ONLY BE CALLED ONCE IN THE CONTROL LOOP.
     */
    public void recordDistance() {
        byte[] byteArr = lidar.read(NUM_OF_BYTES);
        // TODO: Check if first 2 bytes match frame header and handle senario where it
        // doesn't
        int distLow8 = Byte.toUnsignedInt(byteArr[2]);
        int distHigh8 = Byte.toUnsignedInt(byteArr[3]);

        int distance = distLow8 + (distHigh8 << 8);

        if (distance == 0xFFFF) {
            distance = -1;
        }
        lastMeasurement = distance;
    }

    /**
     * Returns the last measurement gotten when {@link #recordDistance()} was last
     * called.
     * 
     * @return The distance, in cm.
     */
    public int getDistanceCM() {
        return lastMeasurement;
    }

}