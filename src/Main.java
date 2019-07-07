import Core.PageLoader;

public class Main{
    public static void main(String[] args) {
        try {
            // inisializing args
//
//        String url = args[0];
//        String path = args[1];
//        int pagesToStop = Integer.parseInt(args[2]);
//        int threadsCount = 1;
//        if(args[3] != null){
//            threadsCount = Integer.parseInt(args[3]);
//        }

            String path = "./Data/";
            String url = "http://www.amozeshi.com/";
            int pagesToStop = 10;
            int threadsCount = 5;
            PageLoader pl = new PageLoader(pagesToStop,threadsCount,path,url);
            pl.savePage(url,path);
            pl.getLinksNums();
            pl.filterLinks();
            System.out.println(pl.links);
            pl.start();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}