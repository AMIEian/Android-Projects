#define NUMBER_OF_DIGITS 4

const int Data = 25;
const int Shift = 27;//6;
const int Store = 26;//7;

const int SecData = 18;
const int SecShift = 21;//19;
const int SecStore = 19;//21;

char str[70];
char *test="$GPRMC";
int temp,i;

char DisplayCodes[11] = {0x3F,0x06,0x5B,0x4F,0x66,0x6D,0x7D,0x07,0x7F,0x6F,0x00};
int TimeArray[6];

void setup() 
  {
    Serial.begin(9600);
    delay(300);
  }
void loop()
  {
    serial1Event();
    if(temp) 
      {
        int str_lenth = i;
        int x = 0, comma = 0;
        String UTC_hour = "";
        String UTC_minut = "";
        String UTC_second = "";
        String UTC_date = "";
        String UTC_month = "";
        String UTC_year = "";
        String str1 = "";
        while(x < str_lenth)
          {
            if(str[x] == ',')
              comma++;
            if(comma == 1)
              {
                x++;
                UTC_hour += str[x++];
                UTC_hour += str[x++];
                UTC_minut += str[x++];
                UTC_minut += str[x++];
                UTC_second += str[x++];
                UTC_second += str[x];
                comma = 2;
              }
            if(comma == 10)
              {
                x++;
                UTC_date += str[x++];
                UTC_date += str[x++];
                UTC_month += str[x++];
                UTC_month += str[x++];
                UTC_year += str[x++];
                UTC_year += str[x];
              }  
            x++;
          }
        int UTC_hourDec = UTC_hour.toInt();
        int UTC_minutDec = UTC_minut.toInt();
        int Second = UTC_second.toInt();
        int Date = UTC_date.toInt();
        int Month = UTC_month.toInt();
        int Year = UTC_year.toInt();
        int Hour = UTC_hourDec + 5;
        if(Hour > 23)
          {
            Hour -= 24;
            Date += 1;
          }
        int Minut = UTC_minutDec + 30;
        if(Minut > 59)
          {
            Minut -= 60;
            Hour += 1;
          }
        temp = 0;
        i = 0;
        x = 0;
        str_lenth = 0;
      }
  }
void serial1Event()
  {
    while(1)
      {
         while(Serial.available())            //checking serial data from GPS
           {
             char inChar = (char)Serial.read();
             str[i] = inChar;                    //store data from GPS into str[]
             i++;
             if(i < 7)                      
               {
                if(str[i-1] != test[i-1])         //checking for $GPRMC sentence
                  {
                    i=0;
                  }
               }
            if(i>65)
              {
                 temp=1;
                 break;
              }
          }
        if(temp)
          break;
      }
  }
