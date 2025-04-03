package com.example.forzautils.viewModels.tuningViewModel

import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.toPrecision
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TuningViewModel : ViewModel() {
  private val TAG = "TuningViewModel"
  val drivetrainTypes = listOf(
    "FWD",
    "RWD",
    "AWD"
  )
  val engineLayouts = listOf(
    "Front",
    "Mid",
    "Rear"
  )
  private val hzConst_lowHeight = 3.2f
  private val hzConst_highHeight = 1.8f
  private val rollCageFrontBump = 1.05f
  private val rollCageRearBump = 1.05f
  private val rollCageFrontRebound = 1.05f
  private val rollCageRearRebound = 1.05f
  private val rollCageFrontARB = 0.85f
  private val rollCageRearARB = 0.85f
  private val baseARB = 15
  private val defaultWeight = 3000f
  private val defaultDistribution = 53f
  private val defaultRearDistribution = 47f
  private val defaultRideHeight = 4.0f
  private var targetHzFront = 2.6f
  private var targetHzRear = 2.6f
  private var hasRollCage = false
  private var drivetrain = drivetrainTypes[0]
  private var engineLayout = engineLayouts[0]

  // region State Flow Variables
  private val _frontDistribution = MutableStateFlow(defaultDistribution)
  val frontDistribution get(): StateFlow<Float> = _frontDistribution

  private val _rearDistribution = MutableStateFlow(defaultRearDistribution)
  val rearDistribution get():StateFlow<Float> = _rearDistribution

  private val _frontWeight = MutableStateFlow(0f)
  val frontWeight: StateFlow<Float> get() = _frontWeight

  private val _rearWeight = MutableStateFlow(0f)
  val rearWeight get(): StateFlow<Float> = _rearWeight

  private val _frontCornerWeight = MutableStateFlow(0f)
  val frontCornerWeight get(): StateFlow<Float> = _frontCornerWeight

  private val _rearCornerWeight = MutableStateFlow(0f)
  val rearCornerWeight get(): StateFlow<Float> = _rearCornerWeight

  private val _frontRideHeight = MutableStateFlow(defaultRideHeight)
  val frontRideHeight get(): StateFlow<Float> = _frontRideHeight

  private val _rearRideHeight = MutableStateFlow(defaultRideHeight)
  val rearRideHeight get(): StateFlow<Float> = _rearRideHeight

  private val _totalWeight = MutableStateFlow(defaultWeight)
  val totalWeight get(): StateFlow<Float> = _totalWeight

  private val _frontSpringRate = MutableStateFlow(0f)
  val frontSpringRate get(): StateFlow<Float> = _frontSpringRate

  private val _rearSpringRate = MutableStateFlow(0f)
  val rearSpringRate get(): StateFlow<Float> = _rearSpringRate

  private val _frontBump = MutableStateFlow(0f)
  val frontBump get(): StateFlow<Float> = _frontBump

  private val _rearBump = MutableStateFlow(0f)
  val rearBump get(): StateFlow<Float> = _rearBump

  private val _frontRebound = MutableStateFlow(0f)
  val frontRebound get(): StateFlow<Float> = _frontRebound

  private val _rearRebound = MutableStateFlow(0f)
  val rearRebound get(): StateFlow<Float> = _rearRebound

  private val _frontARB = MutableStateFlow(0f)
  val frontARB get(): StateFlow<Float> = _frontARB

  private val _rearARB = MutableStateFlow(0f)
  val rearARB get(): StateFlow<Float> = _rearARB
  //endregion

  init {
    updateCalculatedValues()
  }

  fun setRollCage(hasRollCage: Boolean) {
    this.hasRollCage = hasRollCage
    updateCalculatedValues()
  }

  fun setEngineLayout(engineLayout: String) {
    this.engineLayout = engineLayout
    updateCalculatedValues()
  }

  fun setDrivetrain(drivetrain: String) {
    this.drivetrain = drivetrain
    updateCalculatedValues()
  }

  fun setFrontRideHeight(height: String) {
    val parsed = height.toFloatOrNull() ?: defaultRideHeight
    _frontRideHeight.value = parsed
    targetHzFront = calculateTargetHz(parsed)
    updateCalculatedValues()
  }

  fun setRearRideHeight(height: String) {
    val parsed = height.toFloatOrNull() ?: defaultRideHeight
    _rearRideHeight.value = parsed
    targetHzRear = calculateTargetHz(parsed)
    updateCalculatedValues()
  }

  fun setFrontDistribution(distribution: String) {
    val parsed = distribution.toFloatOrNull() ?: defaultDistribution
    _frontDistribution.value = parsed
    _rearDistribution.value = 100 - parsed
    updateCalculatedValues()
  }

  fun setRearDistribution(distribution: String) {
    val parsed = distribution.toFloatOrNull() ?: defaultRearDistribution
    _rearDistribution.value = parsed
    _frontDistribution.value = 100 - parsed
    updateCalculatedValues()
  }

  fun setTotalWeight(vehicleWeight: String) {
    val parsed = vehicleWeight.toFloatOrNull() ?: defaultWeight
    _totalWeight.value = parsed
    updateCalculatedValues()
  }

  private fun updateCalculatedValues() {
    _frontWeight.value = calculateWeightDistribution(totalWeight.value, frontDistribution.value)
    _rearWeight.value = calculateWeightDistribution(totalWeight.value, rearDistribution.value)
    setFrontCornerWeight()
    setRearCornerWeight()
    _frontSpringRate.value = calculateSpringRate(
      frontWeight.value, frontRideHeight.value, targetHzFront
    )
    _rearSpringRate.value = calculateSpringRate(
      rearWeight.value, rearRideHeight.value, targetHzRear
    )
    _frontBump.value = calculateBump(frontSpringRate.value)
    _rearBump.value = calculateBump(rearSpringRate.value)
    _frontRebound.value = calculateRebound(frontBump.value)
    _rearRebound.value = calculateRebound(rearBump.value)
    _frontARB.value = calculateARB(
      frontSpringRate.value,
      frontSpringRate.value + rearSpringRate.value,
      frontDistribution.value
    )
    _rearARB.value = calculateARB(
      rearSpringRate.value,
      frontSpringRate.value + rearSpringRate.value,
      rearDistribution.value
    )
    adjustForLayout()
    adjustForRollCage()
  }

  private fun adjustForLayout() {
    if (engineLayout == engineLayouts[0]) {
      if (drivetrain == drivetrainTypes[0]) {
        _frontBump.value *= 1.15f
        _rearBump.value *= 0.9f
        _rearRebound.value *= 0.9f
        _frontARB.value *= 1.15f
        _rearARB.value *= 0.85f
      } else if (drivetrain == drivetrainTypes[1]) {
        _frontRebound.value *= 0.9f
        _rearRebound.value *= 1.05f
        _frontARB.value *= 0.9f
        _rearARB.value *= 1.05f
      } else if (drivetrain == drivetrainTypes[2]) {
        _frontBump.value *= 1.1f
        _rearRebound.value *= 0.95f
        _frontARB.value *= 1.1f
        _rearARB.value *= 0.95f
      }
    } else if (engineLayout == engineLayouts[1]) {
      if (drivetrain == drivetrainTypes[0]) {
        _frontBump.value *= 1.1f
        _rearRebound.value *= 0.9f
        _frontARB.value *= 1.1f
        _rearARB.value *= 0.9f
      } else if (drivetrain == drivetrainTypes[1]) {
        _frontRebound.value *= 0.95f
        _rearRebound.value *= 1.05f
        _frontARB.value *= 0.95f
        _rearARB.value *= 1.05f
      } else if (drivetrain == drivetrainTypes[2]) {
        _frontARB.value *= 1.05f
      }
    } else if (engineLayout == engineLayouts[2]) {
      if (drivetrain == drivetrainTypes[0]) {
        _frontBump.value *= 1.1f
        _rearRebound.value *= 0.9f
        _frontARB.value *= 1.1f
        _rearARB.value *= 0.9f
      } else if (drivetrain == drivetrainTypes[1]) {
        _frontRebound.value *= 0.9f
        _rearBump.value *= 1.05f
        _rearRebound.value *= 1.1f
        _frontARB.value *= 0.85f
        _rearARB.value *= 1.1f
      } else if (drivetrain == drivetrainTypes[2]) {
        _frontRebound.value *= 0.95f
        _rearRebound.value *= 1.1f
        _frontARB.value *= 0.95f
        _rearARB.value *= 1.05f
      }
    }
  }

  private fun adjustForRollCage() {
    if (hasRollCage) {
      _frontBump.value *= rollCageFrontBump
      _rearBump.value *= rollCageRearBump
      _frontRebound.value *= rollCageFrontRebound
      _rearRebound.value *= rollCageRearRebound
      _frontARB.value *= rollCageFrontARB
      _rearARB.value *= rollCageRearARB
    }
  }

  private fun calculateTargetHz(rideHeight: Float): Float {
    if (rideHeight <= 2.0f) {
      return hzConst_lowHeight
    } else if (rideHeight >= 6.0f) {
      return hzConst_highHeight
    }
    return hzConst_lowHeight - 0.2f * (rideHeight - 2)
  }

  private fun calculateARB(
    springRate: Float,
    totalSpringRate: Float,
    weightDistribution: Float
  ): Float {
    val springDistribution = springRate / totalSpringRate
    val base = baseARB * springDistribution * 2
    val axleSplit = ((weightDistribution / 100) / 0.5).toFloat()
    val arb = base * axleSplit
    return arb
  }

  private fun calculateWeightDistribution(totalWeight: Float, distribution: Float): Float {
    return ((distribution / 100) * totalWeight)
  }

  private fun setFrontCornerWeight() {
    _frontCornerWeight.value = (frontWeight.value / 2)
  }

  private fun setRearCornerWeight() {
    _rearCornerWeight.value = (rearWeight.value / 2)
  }

  private fun calculateSpringRate(weight: Float, rideHeight: Float, targetHz: Float): Float {
    val result = targetHz * (weight / (2 * rideHeight))
    return result
  }

  private fun calculateRebound(bump: Float): Float {
    return (bump * 1.5f)
  }

  private fun calculateBump(springRate: Float): Float {
    return (springRate * 0.011f)
  }
}