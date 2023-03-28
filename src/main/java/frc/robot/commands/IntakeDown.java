// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.util.sendable.SendableRegistry;
import frc.robot.Constants.IntakeConstants;

import frc.robot.subsystems.IntakeTilt;
import frc.robot.subsystems.IntakeWheels;

public class IntakeDown extends PIDCommand {
  private final IntakeTilt intakeTilt;
  private final IntakeWheels intakeWheels;

  public IntakeDown(IntakeTilt intakeTilt, IntakeWheels intakeWheels) {
    super(
      new PIDController(IntakeConstants.kDownP, IntakeConstants.kDownI, IntakeConstants.kDownD),
      intakeTilt::getMeasurement,
      () -> IntakeConstants.kDownPos,
      output -> intakeTilt.useOutput(output, IntakeConstants.kDownPos)
    );

    this.intakeTilt = intakeTilt;
    this.intakeWheels = intakeWheels;

    addRequirements(intakeTilt, intakeWheels);
    SendableRegistry.setSubsystem(m_controller, intakeTilt.getSubsystem());
  }

  @Override
  public void initialize() {
    intakeTilt.disable();
    m_controller.reset();
    intakeWheels.set(IntakeConstants.kIntakeSpeed);
  }

  @Override
  public void end(boolean interrupted) {
    intakeWheels.stop();
  }
}