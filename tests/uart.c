#define txfifo 0x20000000
#define rxfifo 0x20000004
#define txctrl 0x20000008
#define txmark 0x2000000a
#define rxctrl 0x2000000c
#define rxmark 0x2000000e
#define ie     0x20000010
#define ip     0x20000014
#define div    0x20000018





#include "mmio.h"



int main(void)
{




	reg_write32(txctrl,0x01 );                   
	//reg_write32(txmark, 0x00);                 
	//reg_write32(rxmark, 0x00 );                   
    reg_write8(rxctrl , 0x01);                
  //  reg_write32(ie, 0x000 );                             
  //  reg_write32(ip,0x00);      


	reg_write32(div,20);  


    for(int i =1; i<10; i++ ){

     reg_write32(txfifo, i ); 


    for (int k = 0; k<2100; k++){
      asm("");
    }


    if(reg_read32(rxfifo) == i){

    	return 0;
    }

    else {

     return 1;
        }        
	
}
	


	return 0;
}
