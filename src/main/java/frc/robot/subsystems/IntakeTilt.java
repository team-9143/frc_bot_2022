package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import frc.robot.Constants.PhysConstants;
import frc.robot.Constants.DeviceConstants;
import frc.robot.Constants.IntakeConstants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj2.command.FunctionalCommand;

public class IntakeTilt extends PIDSubsystem {
  private static IntakeTilt m_instance;

  /** @return the singleton instance */
  public static synchronized IntakeTilt getInstance() {
    if (m_instance == null) {
      m_instance = new IntakeTilt();
    }
    return m_instance;
  }

  private static final CANSparkMax l_motor = new CANSparkMax(DeviceConstants.kIntakeTiltLeftID, MotorType.kBrushless);
  private static final CANSparkMax r_motor = new CANSparkMax(DeviceConstants.kIntakeTiltRightID, MotorType.kBrushless);

  private static final RelativeEncoder l_encoder = l_motor.getEncoder();
  private static final RelativeEncoder r_encoder = r_motor.getEncoder();

  private IntakeTilt() {
    super(new PIDController(IntakeConstants.kSteadyP, IntakeConstants.kSteadyI, IntakeConstants.kSteadyD));

    r_motor.follow(l_motor, true);

    l_encoder.setPositionConversionFactor(PhysConstants.kTiltGearbox);
    l_encoder.setVelocityConversionFactor(PhysConstants.kTiltGearbox);
    r_encoder.setPositionConversionFactor(PhysConstants.kTiltGearbox);
    r_encoder.setVelocityConversionFactor(PhysConstants.kTiltGearbox);

    l_encoder.setMeasurementPeriod(20);
    r_encoder.setMeasurementPeriod(20);

    l_encoder.setPosition(0);
    r_encoder.setPosition(0);

    m_controller.setIntegratorRange(-IntakeConstants.kTiltMaxSpeed, IntakeConstants.kTiltMaxSpeed);
    m_controller.setSetpoint(IntakeConstants.kUpPos);

    enable();
  }

  @Override
  public void useOutput(double output, double setpoint) {
    // Use the output here
    l_motor.set(Math.max(-IntakeConstants.kTiltMaxSpeed, Math.min(output, IntakeConstants.kTiltMaxSpeed)));
  }

  @Override
  public double getMeasurement() {
    // Return the process variable measurement here
    return (l_encoder.getPosition() + r_encoder.getPosition())/2;
  }

  public void resetEncoder() {
    l_encoder.setPosition(IntakeConstants.kUpPos);
    r_encoder.setPosition(IntakeConstants.kUpPos);
  }

  public void stop() {
    disable();
  }

  public void autoAlign() {
    new FunctionalCommand(
      () -> useOutput(IntakeConstants.kAutoAlignSpeed, IntakeConstants.kUpPos),
      () -> {},
      interrupted -> {
        // TODO(autoAlign): Test and tune up position offset (currently 9 degrees back)
        l_encoder.setPosition(IntakeConstants.kUpPos - 0.025);
        r_encoder.setPosition(IntakeConstants.kUpPos - 0.025);
      },
      () -> (l_motor.getOutputCurrent() > IntakeConstants.kMaxCurrent) || (r_motor.getOutputCurrent() > IntakeConstants.kMaxCurrent),
      this
    ).schedule();
  }
}