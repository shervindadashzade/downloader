package Core;

import Core.PageLoader;

public class DownloadThread extends Thread {
    PageLoader pageLoader;
    public DownloadThread(PageLoader pl){
        pageLoader = pl;
    }
    public void run(){
        try {
            while (pageLoader.links.size() > 0) {
                String link;
                synchronized (pageLoader.links) {
                    link = pageLoader.links.remove();
                    pageLoader.links.notifyAll();
                }
                System.out.println("link is : "+link);
                System.out.println(pageLoader.links.size());
                if (pageLoader.isHtmlFile(link) && pageLoader.isInDomin(link, pageLoader.url)) {
                    pageLoader.savePage(link, pageLoader.path);
                } else {
                    pageLoader.saveFile(link, pageLoader.path);
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
