#include <avr/io.h>                         // AVR 기본 include
#include <util/delay.h>
#include "bluetooth.h"

#define		FALSE			0
#define		TRUE			1

#define 	STATE_OFF		0
#define 	STATE_ON_1		1
#define 	STATE_ON_2		2
#define 	STATE_ON_3		3
#define 	STATE_AUTO		4

volatile int isButtonClicked = FALSE;
volatile int ledState = STATE_OFF;

void setNextState(){
	ledState = (ledState+1)%5;
}

//Kit 기본 LED를 켠다.
void setKitLed(int count){
	int i;

	PORTA = 0;
	for(i=0;i<count;i++){
		PORTA |= 1 << i;
	}
}

void setLight(){

}

void onButtonClicked(){
	setNextState();

}

void init(){
	// 전등(LED) 포트 = PA7~PA0 : 출력
	DDRA = 0xff;

	//ADC 초기화
	ADMUX = 0x00;
	ADCSRA = 0x87;

	init_uart1();		// UART1 초기화
}

void checkBluetooth(){
	volatile char c = getchar1();     // 스마트폰으로부터 명령(데이터)을 받아서
	if(c==1){
		isButtonClicked = TRUE;
		c = 0;
	}
}

unsigned short read_adc()
{
	unsigned char adc_low, adc_high;
	unsigned short value;
	ADCSRA|=0x40;
	while((ADCSRA & 0x10) !=0x10);
	adc_low=ADCL;
	adc_high=ADCH;
	value=((unsigned short)adc_high<<8)|(unsigned short)adc_low;
	return value;
}

void show_adc()
{
	volatile unsigned short value = 0;
	setKitLed(1);
	while(1)
	{
		value = read_adc();
		//printf("value = %d\n",value);
		setKitLed(2);
		if(value<200)setKitLed(6);
		else if(value<400)setKitLed(5);
		else if(value<600)setKitLed(4);
		else if(value<800)setKitLed(3);
		else if(value<1000)setKitLed(2);

		_delay_ms(1000);
	}
}

/**
 * 블루투스나 스위치의 명령이 들어왔을 때 적절한 처리를 하는 함수
 */
#define DEBUG 1

void run(){

#if DEBUG
	show_adc();

#else

	while(1)                       // 명령을 받아서 실행
    {
		checkBluetooth();		

		if(isButtonClicked){
			setNextState();
			isButtonClicked = FALSE;
		}

		setKitLed(ledState);
		setLight();

         
    }

#endif
}

int main()
{

    init();
    
 	run();

}
