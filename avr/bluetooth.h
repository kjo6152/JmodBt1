#ifndef __BLUETOOTH__H__
#define __BLUETOOTH__H__

#include <avr/io.h>                         // AVR �⺻ include


//������� �ʱ�ȭ �Լ�
void init_bluetooth()                               
{

    UCSR1B = 0x18;           // �۽� Transmit(TX), Receive(RX) Enable
    UCSR1C = 0x06;          // UART Mode, 8 Bit Data, No Parity, 1 Stop Bit

    UBRR1H = 0;  // Baudrate ����
    UBRR1L = 8;   // 16Mhz, 115200 baud

}

// 1���� ���ڸ� ����Ʈ�������� ������ �Լ�
void putchar1(char c)                      
{
	//������ �۽� �غ� �Ǿ����� üũ
     while(!(UCSR1A & (1<<UDRE))) ;           
	//�۽��� ������ ����                           
     UDR1 = c;                                             
}

// 1���� ���ڸ� ����Ʈ�����κ��� �޴� �Լ�
char getchar1()                               // 1 ���ڸ� ����(receive)�ϴ� �Լ�
{
	//������ ���� �غ� �Ǿ����� üũ
     if ((UCSR1A & (1<<RXC1))) {
	 	return(UDR1);						
	 }           
	//UDR1���� ���� �����͸� ������                               
     return 0;								
}

// ����Ʈ�����κ��� ���(������)�� �޾Ƽ� �������ְ� ��ư�� ���������� üũ�ϴ� �Լ�
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
