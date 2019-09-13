/*    */ package bio.igm.entities;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Transcript
/*    */ {
/*    */   String refseq;
/* 16 */   Map<Integer, Exons> exons = new HashMap();
/* 17 */   int numOfExons = 0;
/*    */   String sequence;
/*    */   
/*    */   public Transcript(String id)
/*    */   {
/* 22 */     this.refseq = id;
/*    */   }
/*    */   
/* 25 */   public void addExon(Exons e) { this.exons.put(Integer.valueOf(e.getOrder()), e); }
/*    */   
/*    */   public Map<Integer, Exons> getExons()
/*    */   {
/* 29 */     return this.exons;
/*    */   }
/*    */   
/*    */   public void setExons(Map<Integer, Exons> exons) {
/* 33 */     this.exons = exons;
/*    */   }
/*    */   
/*    */   public int getNumOfExons() {
/* 37 */     this.numOfExons = this.exons.size();
/* 38 */     return this.numOfExons;
/*    */   }
/*    */   
/*    */   public void setNumOfExons(int numOfExons) {
/* 42 */     this.numOfExons = numOfExons;
/*    */   }
/*    */   
/*    */   public String getRefseq() {
/* 46 */     return this.refseq;
/*    */   }
/*    */   
/*    */   public void setRefseq(String refseq) {
/* 50 */     this.refseq = refseq;
/*    */   }
/*    */   
/*    */   public String getSequence() {
/* 54 */     return this.sequence;
/*    */   }
/*    */   
/*    */   public void setSequence(String sequence) {
/* 58 */     this.sequence = sequence;
/*    */   }
/*    */ }

