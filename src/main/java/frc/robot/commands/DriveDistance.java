package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.math.controller.PIDController;
import frc.robot.Constants.DrivetrainConstants;

import java.util.Set;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystems.Drivetrain;

public class DriveDistance extends CommandBase {
  private static final Drivetrain drivetrain = Drivetrain.getInstance();
  public static final PIDController m_controller = new PIDController(DrivetrainConstants.kDistP, DrivetrainConstants.kDistI, DrivetrainConstants.kDistD);

  private double distance; // UNIT: inches

  public DriveDistance(double distance) {
    this.distance = distance;
  }

  @Override
  public void initialize() {
    m_controller.reset();
    drivetrain.resetEncoders();
  }

  @Override
  public void execute() {
    drivetrain.moveStraight(Math.max(-DrivetrainConstants.kDistMaxSpeed, Math.min(
      m_controller.calculate(drivetrain.getAvgPosition(), distance),
    DrivetrainConstants.kDistMaxSpeed)));
  }

  @Override
  public boolean isFinished() {
    return m_controller.atSetpoint();
  }

  @Override
  public void end(boolean interrupted) {
    Drivetrain.stop();
  }

  @Override
  public Set<Subsystem> getRequirements() {
    return Set.of(drivetrain);
  }
}