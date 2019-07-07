package Core;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class PageLoader{
   public BlockingQueue<String> links = new LinkedBlockingQueue<>();
   public int nowLevel = 0;
   public int level = 0;
   public int threads = 0;
   public String path = "";
   public String url = "";
   public ArrayList<DownloadThread> threadsArray = new ArrayList<>();
    public PageLoader(int level, int threads, String path, String url) {
        this.level = level;
        this.threads = threads;
        this.path = path;
        this.url = url;
    }

    public void start(){
        for(int i=0;i<threads;i++){
            DownloadThread downloadThread = new DownloadThread(this);
            threadsArray.add(downloadThread);
            downloadThread.start();
        }
    }

    public boolean savePage(String surl, String path){
       try {
           if(nowLevel == level) {
               return false;
           }else {
               Document doc = Jsoup.connect(surl).get();
               File file = new File(path + doc.toString().hashCode() + ".html");
               Elements elements = doc.getElementsByTag("img");
               for (Element element : elements) {
                   String src = fixUrl(element.attributes().get("src"), surl);
                   links.add(src);
               }
               FileWriter fw = new FileWriter(file);
               fw.write(doc.toString());
               fw.flush();
               fw.close();
               nowLevel++;
               return true;
           }
       }catch (Exception e){
           System.out.println(e);
           return false;
       }
   }
   public String fixUrl(String url,String base){
       if(url.startsWith("/")) {
           url = base.substring(0, base.length() - 1) + url;
       }
       return url;
   }
   public void getLinks(String url){
       try {
           Document doc = Jsoup.connect(url).get();
           Elements linksElement = doc.select("a[href]");
           for(Element element:linksElement){
               String link = element.attributes().get("href");
               link = fixUrl(link,url);
               if(isInDomin(link,url)){
                   links.add(link);
               }
           }
       }catch (Exception e){
           System.out.println(e);
       }
   }
   public void getLinksNums(){
        String furl = url;
        getLinks(url);
        for(String link : links){
            if(links.size()>level){
                break;
            }else{
                getLinks(link);
            }
        }
   }
   public void filterLinks(){
        BlockingQueue<String> linkFilter = new LinkedBlockingQueue<>();
        int i =0;
        for(String link : links){
            if(i>=level)
                break;
            linkFilter.add(link);
            i++;
        }
        links = linkFilter;
   }
   public boolean saveFile(String surl,String path){
       try {
           URL url = new URL(surl);
           String[] parts = surl.split("/");
           String fileName = parts[parts.length-1];
           File file = new File(path+"files/"+fileName);
           InputStream in = url.openStream();
           FileOutputStream fos = new FileOutputStream(file);

           byte[] buffer = new byte[1024];

           int length = 0;

           while ((length = in.read(buffer))>0){
               fos.write(buffer,0,length);
           }
           in.close();
           fos.close();

           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
   }

   public boolean isHtmlFile(String surl){
       try {
           URL url = new URL(surl);
           URLConnection urlCon = url.openConnection();
           String type = urlCon.getHeaderField("content-type");
           if(type.contains("text/html"))
               return true;
           else
               return false;
       }catch (Exception e){
           return false;
       }
   }
   public boolean isInDomin(String url,String base){
       base = base.substring(8);
       String[] parts = base.split("/");
       base = parts[0];
       if(url.contains(base.substring(8))){
           return true;
       }else
           return false;
   }
}