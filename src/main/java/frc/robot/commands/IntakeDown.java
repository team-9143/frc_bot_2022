package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants.IntakeConstants;

import frc.robot.subsystems.IntakeTilt;

public class IntakeDown extends PIDCommand {
  public static final PIDController m_controller = new PIDController(IntakeConstants.kDownP, IntakeConstants.kDownI, IntakeConstants.kDownD);

  private final IntakeTilt intakeTilt;

  public IntakeDown(IntakeTilt intakeTilt) {
    super(
      m_controller,
      intakeTilt::getMeasurement,
      () -> IntakeConstants.kDownPos,
      output -> intakeTilt.useOutput(output, IntakeConstants.kDownPos)
    );

    this.intakeTilt = intakeTilt;

    addRequirements(intakeTilt);
  }

  @Override
  public void initialize() {
    intakeTilt.disable();
    m_controller.reset();
  }
}