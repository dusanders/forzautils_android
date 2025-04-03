package com.example.forzautils.viewModels.tuningViewModel

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.toPrecision
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

class TuningViewModel: ViewModel() {
  private val TAG = "TuningViewModel"
  private val defaultWeight = 3000f
  private val defaultDistribution = 53f
  private val defaultRearDistribution = 47f
  private val defaultRideHeight = 4.0f

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

  init {
    updateCalculatedValues()
  }

  fun setFrontRideHeight(height: String) {
    val parsed = height.toFloatOrNull() ?: defaultRideHeight
    _frontRideHeight.value = parsed.toPrecision(2)
    updateCalculatedValues()
  }

  fun setRearRideHeight(height: String) {
    val parsed = height.toFloatOrNull() ?: defaultRideHeight
    _rearRideHeight.value = parsed.toPrecision(2)
    updateCalculatedValues()
  }

  fun setFrontDistribution(distribution: String) {
    val parsed = distribution.toFloatOrNull() ?: defaultDistribution
    _frontDistribution.value = parsed.toPrecision(2)
    _rearDistribution.value = 100 - parsed.toPrecision(2)
    updateCalculatedValues()
  }

  fun setRearDistribution(distribution: String) {
    val parsed = distribution.toFloatOrNull() ?: defaultRearDistribution
    _rearDistribution.value = parsed.toPrecision(2)
    _frontDistribution.value = 100 - parsed.toPrecision(2)
    updateCalculatedValues()
  }

  fun setTotalWeight(vehicleWeight: String) {
    val parsed = vehicleWeight.toFloatOrNull() ?: defaultWeight
    _totalWeight.value = parsed.toPrecision(2)
    updateCalculatedValues()
  }

  private fun updateCalculatedValues() {
    _frontWeight.value = calculateWeightDistribution(totalWeight.value, frontDistribution.value)
    _rearWeight.value = calculateWeightDistribution(totalWeight.value, rearDistribution.value)
    setFrontCornerWeight()
    setRearCornerWeight()
    _frontSpringRate.value = calculateSpringRate(frontCornerWeight.value, frontRideHeight.value)
    _rearSpringRate.value = calculateSpringRate(rearCornerWeight.value, rearRideHeight.value)
    _frontBump.value = calculateBump(frontSpringRate.value)
    _rearBump.value = calculateBump(rearSpringRate.value)
    _frontRebound.value = calculateRebound(frontBump.value)
    _rearRebound.value = calculateRebound(rearBump.value)
    _frontARB.value = calculateFrontARB(_frontSpringRate.value, _rearSpringRate.value)
    _rearARB.value = calculateRearARB(_frontSpringRate.value, _rearSpringRate.value)
  }

  private fun calculateFrontARB(frontSpringRate: Float, rearSpringRate: Float): Float {
    val normalizer = 15 // scale of 0 - 30
    val totalSpringRate = frontSpringRate + rearSpringRate
    val frontProportion = frontSpringRate / totalSpringRate
    val base = normalizer * frontProportion * 2
    val axleSplit = ((_frontDistribution.value / 100) / 0.5).toFloat()
    val arb = base * axleSplit
    return arb.toPrecision(2)
  }

  private fun calculateRearARB(frontSpringRate: Float, rearSpringRate: Float): Float {
    val normalizer = 15 // scale of 0 - 30
    val totalSpringRate = frontSpringRate + rearSpringRate
    val rearProportion = rearSpringRate / totalSpringRate
    val base = normalizer * rearProportion * 2
    val axleSplit = ((_rearDistribution.value / 100) / 0.5).toFloat()
    val arb = base * axleSplit
    return arb.toPrecision(2)
  }

  private fun calculateWeightDistribution(totalWeight: Float, distribution: Float): Float {
    return ((distribution / 100) * totalWeight).toPrecision(2)
  }

  private fun setFrontCornerWeight() {
    _frontCornerWeight.value = (frontWeight.value / 2).toPrecision(2)
  }

  private fun setRearCornerWeight() {
    _rearCornerWeight.value = (rearWeight.value / 2).toPrecision(2)
  }

  private fun calculateSpringRate(axleWeight: Float, rideHeight: Float): Float {
    val targetHz = 2.8875f
    val totalMass = axleWeight * 2
    val rate = targetHz * (totalMass / (2 * rideHeight))
    return rate.toPrecision(2)
  }

  private fun calculateRebound(bump: Float): Float {
    return (bump * 1.6f).toPrecision(2)
  }

  private fun calculateBump(springRate: Float): Float {
    return (springRate * 0.011f).toPrecision(2)
  }
}