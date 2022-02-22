// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.util.sendable.SendableBuilder;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.IntakeConstants.*;

public class IntakeSubsystem extends SubsystemBase {
  private WPI_TalonSRX deployLeft;
  private WPI_TalonSRX deployRight;
  
  private WPI_TalonSRX rollerMotor;

  private double currentAngleLeft;
  private double currentAngleRight;


  /** Creates a new IntakeSubsystem. */
  public IntakeSubsystem() {
    // capital variable names are statically imported constants
    deployLeft = new WPI_TalonSRX(DEPLOY_LEFT_MOTOR);
    deployRight = new WPI_TalonSRX(DEPLOY_RIGHT_MOTOR);

    deployRight.setInverted(true);

    deployLeft.configPeakCurrentLimit(CURRENT_LIMIT);
    deployLeft.configPeakCurrentDuration(CURRENT_DURATION);
    deployRight.configPeakCurrentLimit(CURRENT_LIMIT);
    deployRight.configPeakCurrentDuration(CURRENT_DURATION);

    rollerMotor = new WPI_TalonSRX(ROLLER_MOTOR);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    currentAngleLeft = (deployLeft.getSelectedSensorPosition() / 8192) * 360 + 62.5;
    currentAngleRight = (deployLeft.getSelectedSensorPosition() / 8192) * 360 + 62.5;
  }

  /** 
  * Deploys the intake device for picking up cargo
  */
  public void deployIntake()
  {
    if(currentAngleLeft >= 55.0 && currentAngleRight >= 55.0)
    {
      deployLeft.set(0);
      deployRight.set(0);
    }
    else
    {
      deployLeft.set(DEPLOY_SPEED);
      deployRight.set(DEPLOY_SPEED);
    }
  }

  /** 
  * Retracts the intake device
  */
  public void retractIntake(){
  
    if(currentAngleLeft <= 0.0 && currentAngleRight >= 0.0)
    {
      deployLeft.set(0);
      deployRight.set(0);
    }
    else
    {
      deployLeft.set(DEPLOY_SPEED * -1);
      deployRight.set(DEPLOY_SPEED * -1);
    }
  }

  /**
   * Activates the intake rollers to collect cargo
   */
  public void intakeCargo()
  {
    rollerMotor.set(INTAKE_SPEED);
  }

  /**
   * Reverses the intake system to remove jammed cargo
   */
  public void reverseIntakeCargo()
  {
    rollerMotor.set(INTAKE_SPEED * -1);
  }

  /**
   * Stopes the intake rollers
   */
  public void stopIntake(){
    rollerMotor.set(0);
  }

  /**
   * Returns current angle of the deployLeft encoder
   * @return double value
   */
  public double getCurrentAngleLeft() {
    return currentAngleLeft;
  }

  /**
   * Returns the current angle of the deployRight encoder
   * @return double value
   */
  public double getCurrentAngleRight() {
    return currentAngleRight;
  }

  @Override
  public void initSendable(SendableBuilder sendable) {
    sendable.setSmartDashboardType("Intake");
    sendable.addDoubleProperty("Current Angle Left", this::getCurrentAngleLeft, null);
    sendable.addDoubleProperty("Current Angle Right", this::getCurrentAngleRight, null);
  }


  /**
   * Test that each motor controller is connected.
   * 
   * @return a map of the motor's name and a boolean with true if it is connected
   */
  public Map<String, Boolean> test() {
    var motors = new HashMap<String, Boolean>();

    deployLeft.getBusVoltage();
    motors.put("Deploy left motor", deployLeft.getLastError() == ErrorCode.OK);

    deployRight.getBusVoltage();
    motors.put("Deploy right motor", deployRight.getLastError() == ErrorCode.OK);

    rollerMotor.getBusVoltage();
    motors.put("Roller motor", rollerMotor.getLastError() == ErrorCode.OK);
    
    return motors;
  }
}
