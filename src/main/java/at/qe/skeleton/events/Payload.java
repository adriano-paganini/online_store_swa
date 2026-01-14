package at.qe.skeleton.events;


public class Payload<T extends PayloadInterface> implements PayloadInterface{
    T payloadInfo;

    public Payload(T payloadInfo) {
        this.payloadInfo = payloadInfo;
    }

    @Override
    public String getPayloadSubjectLine(){
        return payloadInfo.getPayloadSubjectLine();
    }
    public T getPayloadInfo() {
        return payloadInfo;
    }
    public void setPayloadInfo(T payloadInfo){
        this.payloadInfo= payloadInfo;
    }
}
