

#define PWM_PERIOD 0x2000
#define PWM_DUTY 0x2004
#define PWM_ENABLE 0x2008
#define PWM_OUT 0x200C

#include "mmio.h"

int main(void)
{
  reg_write32(PWM_PERIOD, 30);
  reg_write32(PWM_DUTY, 5);
  reg_write32(PWM_ENABLE, 1);





  return 0;
}
