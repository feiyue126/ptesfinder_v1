/*    */ package bio.igm.utils.init;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileReader;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SplitReads
/*    */ {
/*    */   String path;
/*    */   String out;
/* 22 */   static StringBuilder left = new StringBuilder();
/* 23 */   static StringBuilder right = new StringBuilder();
/*    */   
/*    */   public SplitReads(String path, String out) throws FileNotFoundException, IOException {
/* 26 */     this.out = out;
/* 27 */     this.path = path;
/* 28 */     readFastQ(path);
/*    */   }
/*    */   
/*    */   private void readFastQ(String s) throws FileNotFoundException, IOException
/*    */   {
/* 33 */     BufferedReader br = new BufferedReader(new FileReader(s));
/* 34 */     String p = this.out + "left.fastq";
/* 35 */     String q = this.out + "right.fastq";
/* 36 */     BufferedWriter bwl = new BufferedWriter(new FileWriter(p));
/* 37 */     BufferedWriter bwr = new BufferedWriter(new FileWriter(q));
/* 38 */     String line = "";
/* 39 */     String l = "";
/* 40 */     String r = "";
/*    */     
/* 42 */     int counter = 4;
/* 43 */     int index = 0;
/* 44 */     while ((line = br.readLine()) != null) {
/* 45 */       index = counter % 4;
/* 46 */       switch (index) {
/*    */       case 0: 
/* 48 */         l = line;
/* 49 */         r = line;
/* 50 */         break;
/*    */       case 1: 
/* 52 */         l = line.substring(0, 20);
/* 53 */         r = line.substring(line.length() - 20, line.length());
/* 54 */         break;
/*    */       case 2: 
/* 56 */         l = line;
/* 57 */         r = line;
/* 58 */         break;
/*    */       case 3: 
/* 60 */         l = line.substring(0, 20);
/* 61 */         r = line.substring(line.length() - 20, line.length());
/*    */       }
/* 63 */       bwl.write(l + "\n");
/* 64 */       bwr.write(r + "\n");
/*    */       
/*    */ 
/* 67 */       counter++;
/*    */     }
/* 69 */     bwl.close();
/* 70 */     bwr.close();
/*    */   }
/*    */   
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try
/*    */     {
/* 78 */       String path = args[0];
/* 79 */       String out = args[1];
/* 80 */       new SplitReads(path, out);
/*    */     } catch (FileNotFoundException ex) {
/* 82 */       Logger.getLogger(SplitReads.class.getName()).log(Level.SEVERE, null, ex);
/*    */     } catch (IOException ex) {
/* 84 */       Logger.getLogger(SplitReads.class.getName()).log(Level.SEVERE, null, ex);
/*    */     }
/*    */   }
/*    */ }
