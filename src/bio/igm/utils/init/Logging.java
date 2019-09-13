/*    */ package bio.igm.utils.init;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.logging.FileHandler;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import java.util.logging.SimpleFormatter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Logging
/*    */ {
/*    */   public static FileHandler fh;
/*    */   public static SimpleFormatter formatter;
/*    */   private String classname;
/*    */   
/*    */   public Logging(String path, String classname)
/*    */     throws IOException
/*    */   {
/* 26 */     fh = new FileHandler(path + "/run.log", true);
/*    */     
/* 28 */     formatter = new SimpleFormatter();
/* 29 */     this.classname = classname;
/*    */   }
/*    */   
/*    */   public Logger setup() {
/* 33 */     Logger logger = Logger.getLogger(this.classname);
/* 34 */     logger.setLevel(Level.ALL);
/* 35 */     fh.setFormatter(formatter);
/* 36 */     logger.addHandler(fh);
/* 37 */     logger.setUseParentHandlers(false);
/* 38 */     return logger;
/*    */   }
/*    */ }
