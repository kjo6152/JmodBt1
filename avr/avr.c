#include <avr/io.h>                         // AVR �⺻ include
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

//Kit �⺻ LED�� �Ҵ�.
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

    init_uart1();                 // UART1 �ʱ�ȭ
    DDRA = 0xff;  // ����(LED) ��Ʈ = PA7~PA0 : ���
    DDRB = 0xff;  // ����(����) ��Ʈ = PB4 �� ��ǳ��(����) ��Ʈ = PB7~PB6 : ���
 

    while(1)                       // ����� �޾Ƽ� ����
    {

             c = getchar1();     // ����Ʈ�����κ��� ���(������)�� �޾Ƽ�
             switch(c)              // ����� ������ ���� �Ʒ��� ����
             {

                 case 1:
				 	PORTA = 0xff;
					break;
             }

    }

}
