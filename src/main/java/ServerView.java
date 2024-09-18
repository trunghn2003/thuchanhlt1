public class ServerView {
    public ServerView(){
        new ServerControl();
        showMessage("UDP server is running...");
    }
    public void showMessage(String msg){
        System.out.println(msg);
    }
}