# PWM with Enhanced features in Rocketchip

## Introduction

This version of chipyard contains a PWM module that has enhanced features like availability of multiple PWM outputs, Variable phase shift, Options to get Left,Right or Center-Aligned waveforms etc.

## Usage

The number of outputs can be specified by passing it as a parameter during configuration time.

There are also registers to specify the number of PWM output pulses required. If this value exceeds a threshold, infinite number of pulses are obtained. 

The alignments available are left, right and center. Their complements are also available.

The phase shift register can be used to specify an arbitrary phase shift. 

Combining these features an adjustable deadband can also be obtained.


## Test file

The test parameters are specified in a file PWM.c

The register map starts at 0x2000. There are 7 registers available for each PWM output. They are:

period : used to specify the total period of the waveform in clock cycle

duty  : used to specify the high time in clock cycle

numpulses : used to specify the number of pulses required

align : used to specify the alignment (or its complement). The lower  16 bits specify the alignent. The following numbers must be written to this register to get the necessary alignment.

                               0x01    Left aligned
                               0x02    Complement of Left Aligned
                               0x03    Right Aligned
                               0x04    Complement of Right Aligned
                               0x05    Center Aligned
                               0x06    Complement of Center Aligned
                            
  The upper 16 bits is used to specify the alignment amount in the case of center algned PWM.
  
  shift : This is used to specify the shift amount in clock cycles.
  kill : This register when asserted will disable the PWM outputs.
  master_enable : This register when asserted will simultaneously enable all the PWM outputs.This is mapped to 0x2900
  
  
  The above register values can be written into using the header file mmio.h and using the reg_write32() function.
                            
                            
                            
