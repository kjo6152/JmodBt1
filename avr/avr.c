#include <avr/io.h>                         // AVR 기본 include
#include "bluetooth.h"

#define 	STATE_OFF		0
#define 	STATE_ON_1		1
#define 	STATE_ON_2		2
#define 	STATE_ON_3		3
#define 	STATE_AUTO		4

volatile int ledState = STATE_OFF;

void setNextState(){
	ledState = (ledState+1)%5;
}

//Kit 기본 LED를 켠다.
void setKitLed(){
	int i;

	PORTA = 0;
	for(i=0;i<STATE_AUTO;i++){
		PORTA |= 1 << i;
	}
}

void setLight(){

}

void onButtonClicked(){
	setNextState();

}

int main()
{

    char c;

    init_uart1();                 // UART1 초기화
    DDRA = 0xff;  // 전등(LED) 포트 = PA7~PA0 : 출력
    DDRB = 0xff;  // 버저(버저) 포트 = PB4 및 선풍기(모터) 포트 = PB7~PB6 : 출력
 

    while(1)                       // 명령을 받아서 실행
    {

             c = getchar1();     // 스마트폰으로부터 명령(데이터)을 받아서
             switch(c)              // 명령의 종류에 따라 아래를 실행
             {

                 case 1:
				 	PORTA = 0xff;
					break;
             }

    }

}
