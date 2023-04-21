package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.OI;
import frc.robot.Constants.PhysConstants;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.Constants.DeviceConstants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj2.command.RunCommand;

/** Controls the robot drivetrain. */
public class Drivetrain extends SubsystemBase {
  private static Drivetrain m_instance;

  /** @return the singleton instance */
  public static synchronized Drivetrain getInstance() {
    if (m_instance == null) {
      m_instance = new Drivetrain();
    }
    return m_instance;
  }

  // Initialize motors, encoders, and differential drive
  private static final CANSparkMax
    fl_motor = new CANSparkMax(DeviceConstants.kFrontLeftID, MotorType.kBrushless),
    bl_motor = new CANSparkMax(DeviceConstants.kBackLeftID, MotorType.kBrushless),
    fr_motor = new CANSparkMax(DeviceConstants.kFrontRightID, MotorType.kBrushless),
    br_motor = new CANSparkMax(DeviceConstants.kBackRightID, MotorType.kBrushless);

  private static final RelativeEncoder l_encoder = fl_motor.getEncoder();
  private static final RelativeEncoder r_encoder = fr_motor.getEncoder();

  private static final DifferentialDrive robotDrive = new DifferentialDrive(fl_motor, fr_motor);

  private Drivetrain() {
    // IMPORTANT: Ensure that motors on the same side have the same output
    bl_motor.follow(fl_motor);
    br_motor.follow(fr_motor);

    l_encoder.setPositionConversionFactor(PhysConstants.kWheelCircumference * PhysConstants.kDrivetrainGearbox);
    l_encoder.setVelocityConversionFactor(PhysConstants.kWheelCircumference * PhysConstants.kDrivetrainGearbox / 60);
    l_encoder.setMeasurementPeriod(20);
    l_encoder.setPosition(0);

    r_encoder.setPositionConversionFactor(PhysConstants.kWheelCircumference * PhysConstants.kDrivetrainGearbox);
    r_encoder.setVelocityConversionFactor(PhysConstants.kWheelCircumference * PhysConstants.kDrivetrainGearbox / 60);
    r_encoder.setMeasurementPeriod(20);
    r_encoder.setPosition(0);

    // Teleop drive: single joystick or turn in place with triggers
    setDefaultCommand(new RunCommand(
      () -> {
        if (Math.abs(OI.driver_cntlr.getTriggers()) > 0.05) {
          // Turn in place, input from triggers
          turnInPlace(DrivetrainConstants.kTurnMult * OI.driver_cntlr.getTriggers());
        } else {
          // Regular drive, input from left stick
          robotDrive.arcadeDrive(DrivetrainConstants.kTurnMult * OI.driver_cntlr.getLeftX(), DrivetrainConstants.kSpeedMult * OI.driver_cntlr.getLeftY(), true);
        }
      },
      this
    ));
  }

  public void turnInPlace(double rotationSpeed) {
    robotDrive.tankDrive(rotationSpeed, rotationSpeed, false);
  }

  public void moveStraight(double speed) {
    robotDrive.tankDrive(speed, -speed, false);
  }

  /** @return the speed of the left motors */
  public double getLeft() {return fl_motor.get();}
  /** @return the speed of the right motors */
  public double getRight() {return fr_motor.get();}

  /** @return the average position of the drivetrain encoders */
  public double getPosition() {
    return (l_encoder.getPosition() - r_encoder.getPosition())/2;

    // For simulation
    // if (frc.robot.shuffleboard.SimulationTab.drivetrainPos_sim == null) {
    //   return 0;
    // } else {
    //   return frc.robot.shuffleboard.SimulationTab.drivetrainPos_sim.getDouble(0);
    // }
  }

  public void resetEncoders() {
    l_encoder.setPosition(0);
    r_encoder.setPosition(0);

    // For simulation
    // frc.robot.shuffleboard.SimulationTab.drivetrainPos_sim.setDouble(0);
  }

  public static void stop() {
    robotDrive.stopMotor();
  }
}