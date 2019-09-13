/*    */ package bio.igm.entities;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CanJunctions
/*    */   implements Serializable
/*    */ {
/*    */   private String junction;
/*    */   private String refseq;
/*    */   private int count;
/*    */   private int prime5;
/*    */   private int prime3;
/*    */   
/*    */   public CanJunctions() {}
/*    */   
/*    */   public CanJunctions(String string)
/*    */   {
/* 28 */     this.junction = string.split("\t")[0];
/* 29 */     this.refseq = this.junction.split("\\.")[0];
/* 30 */     this.prime3 = Integer.parseInt(this.junction.split("\\.")[2]);
/* 31 */     this.prime5 = Integer.parseInt(this.junction.split("\\.")[1]);
/* 32 */     this.count = Integer.parseInt(string.split("\t")[1]);
/*    */   }
/*    */   
/*    */   public String getJunction()
/*    */   {
/* 37 */     return this.junction;
/*    */   }
/*    */   
/*    */   public void setJunction(String junction) {
/* 41 */     this.junction = junction;
/*    */   }
/*    */   
/*    */   public String getRefseq() {
/* 45 */     return this.refseq;
/*    */   }
/*    */   
/*    */   public void setRefseq(String refseq) {
/* 49 */     this.refseq = refseq;
/*    */   }
/*    */   
/*    */   public int getCount() {
/* 53 */     return this.count;
/*    */   }
/*    */   
/*    */   public void setCount(int count) {
/* 57 */     this.count = count;
/*    */   }
/*    */   
/*    */   public int getPrime5() {
/* 61 */     return this.prime5;
/*    */   }
/*    */   
/*    */   public void setPrime5(int prime5) {
/* 65 */     this.prime5 = prime5;
/*    */   }
/*    */   
/*    */   public int getPrime3() {
/* 69 */     return this.prime3;
/*    */   }
/*    */   
/*    */   public void setPrime3(int prime3) {
/* 73 */     this.prime3 = prime3;
/*    */   }
/*    */ }


/* Location:              /Users/osagie/Downloads/PTESFinder/code/PTESDiscovery.jar!/bio/igm/entities/CanJunctions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */