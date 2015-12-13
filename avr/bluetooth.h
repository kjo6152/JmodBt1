#ifndef __BLUETOOTH__H__
#define __BLUETOOTH__H__

#include <avr/io.h>                         // AVR 기본 include


//블루투스 초기화 함수
void init_bluetooth()                               
{

    UCSR1B = 0x18;           // 송신 Transmit(TX), Receive(RX) Enable
    UCSR1C = 0x06;          // UART Mode, 8 Bit Data, No Parity, 1 Stop Bit

    UBRR1H = 0;  // Baudrate 세팅
    UBRR1L = 8;   // 16Mhz, 115200 baud

}

// 1개의 문자를 스마트폰측으로 보내는 함수
void putchar1(char c)                      
{
	//데이터 송신 준비가 되었는지 체크
     while(!(UCSR1A & (1<<UDRE))) ;           
	//송신할 데이터 설정                           
     UDR1 = c;                                             
}

// 1개의 문자를 스마트폰으로부터 받는 함수
char getchar1()                               // 1 문자를 수신(receive)하는 함수
{
	//데이터 수신 준비가 되었는지 체크
     if ((UCSR1A & (1<<RXC1))) {
	 	return(UDR1);						
	 }           
	//UDR1에서 수신 데이터를 가져옴                               
     return 0;								
}

// 스마트폰으로부터 명령(데이터)을 받아서 응답해주고 버튼이 눌린것으로 체크하는 함수
int checkBluetooth(){
	volatile char c = getchar1();     
	if(c==1){
		putchar1(c);
		c = 0;
		return 1;
	}
	return 0;
}

#endif
