# OSS_Project 

채팅 앱 오픈소스를 분석하고 개선합니다.

---

|단위|크기|
|------|---|
|1Byte|8bit|
|1KB|1024byte|
|1MB|1024KB|
|1GB|1024MB|
|1TB|1024GB|

----

## ADD Function (MessageAdapterList.java)

```java
   public MESSAGE getMessage(int idx) throws Exception {
        if(idx >= 0)
            return (MESSAGE) items.get(idx);
        else
            throw new Exception("Wrong Index.");

    }

    public MESSAGE getMessage(String content) throws Exception{
        for(Wrapper item : items){
            MESSAGE message = (MESSAGE) item;

            if(message.equals(message)){
                return (MESSAGE) items.get(0);
            }
        }

        return null;
    }

```
