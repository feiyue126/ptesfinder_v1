/*    */ package bio.igm.entities;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExonConstructs
/*    */ {
/*    */   private String id;
/*    */   
/*    */ 
/*    */ 
/*    */   private int junction;
/*    */   
/*    */ 
/*    */   private String sequence;
/*    */   
/*    */ 
/*    */   private int[] sizes;
/*    */   
/*    */ 
/*    */ 
/*    */   public ExonConstructs(String id, String exon1, String exon2)
/*    */   {
/* 24 */     this.id = id;
/* 25 */     int x = 0;
/* 26 */     int y = 0;
/* 27 */     this.sizes = new int[2];
/*    */     
/* 29 */     x = exon1.length();
/* 30 */     y = exon2.length();
/* 31 */     this.sequence = (exon1.replace("\r", "") + exon2.replace("\r", ""));
/*    */     
/* 33 */     this.junction = (x - 1);
/* 34 */     this.sizes[0] = x;
/* 35 */     this.sizes[1] = y;
/*    */   }
/*    */   
/*    */   public ExonConstructs(String s, String exon1, String exon2, int segment_size)
/*    */   {
/* 40 */     int x = 0;
/* 41 */     int y = 0;
/* 42 */     this.sizes = new int[2];
/*    */     
/* 44 */     x = exon1.length();
/* 45 */     int x1 = x > segment_size ? segment_size : x;
/* 46 */     y = exon2.length();
/* 47 */     int y1 = y > segment_size ? segment_size : y;
/* 48 */     String seq1 = exon1.replace("\r", "").substring(x - x1);
/* 49 */     String seq2 = exon2.replace("\r", "").substring(0, y1);
/* 50 */     this.sequence = (seq1 + seq2);
/* 51 */     this.id = (s + "." + x1);
/*    */     
/* 53 */     this.junction = x1;
/* 54 */     this.sizes[0] = x;
/* 55 */     this.sizes[1] = y;
/*    */   }
/*    */   
/*    */   public String getId() {
/* 59 */     return this.id;
/*    */   }
/*    */   
/*    */   public void setId(String id) {
/* 63 */     this.id = id;
/*    */   }
/*    */   
/*    */   public int getJunction() {
/* 67 */     return this.junction;
/*    */   }
/*    */   
/*    */   public void setJunction(int junction) {
/* 71 */     this.junction = junction;
/*    */   }
/*    */   
/*    */   public String getSequence() {
/* 75 */     return this.sequence;
/*    */   }
/*    */   
/*    */   public void setSequence(String sequence) {
/* 79 */     this.sequence = sequence;
/*    */   }
/*    */   
/*    */   public int[] getSizes() {
/* 83 */     return this.sizes;
/*    */   }
/*    */   
/*    */   public void setSizes(int[] sizes) {
/* 87 */     this.sizes = sizes;
/*    */   }
/*    */ }
