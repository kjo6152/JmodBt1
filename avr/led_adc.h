#ifndef __LED__ADC__H__
#define __LED__ADC__H__

#include <avr/io.h>                         // AVR 기본 include

//LED 초기화
void init_led(){
	// 전등(LED) 포트 = PA7~PA0 : 출력
	DDRA = 0xff;
}

//조도센서 초기화
void init_adc(){
	ADMUX = 0x00;
	ADCSRA = 0x87;
}

//Kit 기본 LED를 count 만큼 켠다.
void setKitLed(int count){
	int i;

	PORTA = 0;
	for(i=0;i<count;i++){
		PORTA |= 1 << i;
	}
}

//연결된 LED를 count만큼 켠다.
void setLight(int count){

}

//현재 조도 센서의 값을 읽어온다.
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

//Auto 모드일때 조도 센서의 값에 따라서 켜질 LED의 개수를 얻는다.
int get_auto()
{
	volatile unsigned short value = 0;
	
	value = read_adc();

	if(value<400)return 3;
	else if(value<600)return 2;
	else if(value<800)return 1;
	else if(value<1000)return 0;

	return 0;
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

#endif
