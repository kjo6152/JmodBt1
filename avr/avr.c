#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include "bluetooth.h"
#include "led_adc.h"

#define		FALSE			0
#define		TRUE			1

//전등의 상태 DEFINE
//OFF / 1 / 2 / 3 / AUTO로 총 5가지 상태
#define 	STATE_OFF		0
#define 	STATE_ON_1		1
#define 	STATE_ON_2		2
#define 	STATE_ON_3		3
#define 	STATE_AUTO		4

//버튼이 스마트폰이나 킷으로부터 클릭되었는지 확인하는 변수
volatile int isButtonClicked = FALSE;
//현재 LED 상태를 저장하는 변수
volatile int ledState = STATE_OFF;
//몇 개의 LED를 켜야하는지 저장하는 변수
volatile int ledCount = 0;

//전등을 다음 상태로 변경
void setNextState(){
	ledState = (ledState+1)%5;
}

//버튼이 클릭되었을 때 실행되는 함수, 전등을 다음 상태로 변경한다.
void onButtonClicked(){
	setNextState();
}

//스위치 초기화
void init_switch(){
	DDRE = 0xcf; // 1100 1111, INT 4, 5
	EICRB = 0x0a; // 0000 1010 INT 4, 5 => falling
	EIMSK = 0x30; // 0011 0000 INT 4, 5
	sei(); // SREG |= 1<<7; // 1000 0000
}

//스위치1
SIGNAL(SIG_INTERRUPT4){
	isButtonClicked = TRUE;
}

//스위치2
SIGNAL(SIG_INTERRUPT5){
	isButtonClicked = TRUE;

}

//초기화 함수
void init(){
	//조도센서 초기화
	init_adc();
	//LED 초기화
	init_led();
	//스위치 초기화
	init_switch();
	//블루투스 초기화
	init_bluetooth();		
}

/**
 * 블루투스나 스위치의 명령이 들어왔을 때 적절한 처리를 하는 함수
 */
void run(){


	while(1)                       
    {
		//블루투스로부터 버튼 입력이 왔는지 확인한다.
		isButtonClicked |= checkBluetooth();		
		
		//스위치나 블루투스로부터 버튼이 입력되었으면 상태를 변경한다.
		if(isButtonClicked){
			setNextState();
			isButtonClicked = FALSE;
		}

		//AUTO모드일 경우 켤 LED의 개수를 구한다.
		if(ledState==STATE_AUTO){
			ledCount = get_auto();
		}
		else {
			ledCount = ledState;
		}

		//LED를 켠다.
		setKitLed(ledCount);
		setLight(ledCount); 
    }

}

int main()
{
	//디바이스 초기화
    init();
    //작업 수행
 	run();

}
