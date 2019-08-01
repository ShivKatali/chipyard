#define PWM_PERIOD 0x2000
#define PWM_DUTY 0x2020
#define PWM_ALIGN 0x2060
#define PWM_SHIFT 0x2080
#define PWM_NUMPULSE 0x2040
#define PWM_KILL 0x2100
#define PWM_MENABLE 0x2900

#include "mmio.h"
#include <stdlib.h>
#include <stdio.h>
#include <time.h>

int main(void)
{

           
     reg_write32(PWM_PERIOD ,                       7);
     reg_write32(PWM_DUTY ,                         4);                                 //1
	   reg_write32(PWM_NUMPULSE ,                     5);
     reg_write32(PWM_ALIGN ,                     0x01);
     reg_write32(PWM_SHIFT ,                        0);
    


     reg_write32(PWM_PERIOD+4,                      5);
     reg_write32(PWM_DUTY+4,                        2);
	   reg_write32(PWM_NUMPULSE+4,                    6);                                 //2
     reg_write32(PWM_ALIGN+4,                    0x02);
     reg_write32(PWM_SHIFT+4 ,                      1);
       



     reg_write32(PWM_PERIOD + 8,                      6);
     reg_write32(PWM_DUTY + 8,                        4);
	   reg_write32(PWM_NUMPULSE + 8,                    5);                                  //3
     reg_write32(PWM_ALIGN + 8,                  0x03);
     reg_write32(PWM_SHIFT + 8,                     0);
      



     reg_write32(PWM_PERIOD + 12,                  10);
     reg_write32(PWM_DUTY + 12,                     4);
	   reg_write32(PWM_NUMPULSE +12,         0xFFFFFFFF);                                   //4
     reg_write32(PWM_ALIGN +12,                   0x4);
     reg_write32(PWM_SHIFT +12,                     2);
 




     reg_write32(PWM_PERIOD + 16,                     4);                                                
	   reg_write32(PWM_NUMPULSE + 16,                   5); 
     reg_write32(PWM_ALIGN + 16 ,            0x00020005);
     reg_write32(PWM_SHIFT + 16,                      0);




     reg_write32(PWM_PERIOD + 20,                    7);
	   reg_write32(PWM_NUMPULSE + 20,                  5);
     reg_write32(PWM_ALIGN + 20,            0x00020006);
     reg_write32(PWM_SHIFT + 20,                     0);
 



     reg_write32(PWM_PERIOD + 24,                     8);
	   reg_write32(PWM_NUMPULSE + 24 ,                  6);
     reg_write32(PWM_ALIGN + 24,             0x00020005);                                    //7
     reg_write32(PWM_SHIFT + 24 ,                     0);
 



     reg_write32(PWM_PERIOD + 28,                     8);
	   reg_write32(PWM_NUMPULSE + 28,                   5);
     reg_write32(PWM_ALIGN + 28,             0x00060006);                                    
     reg_write32(PWM_SHIFT + 28 ,                     0);




    for (int k = 0; k<2100; k++){
      asm("");
    }



       reg_write32(PWM_MENABLE, 1);



    for (int k = 0; k<200; k++){
      asm("");
    }




     reg_write32(PWM_KILL +4, 1);




    while(1){

    }


	return 0;
}
