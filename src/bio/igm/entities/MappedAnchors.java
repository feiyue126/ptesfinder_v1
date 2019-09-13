/*     */ package bio.igm.entities;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang3.Range;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MappedAnchors
/*     */ {
/*  19 */   boolean junctionSpan = false;
/*  20 */   Exons[] exons = new Exons[2];
/*  21 */   Exons[] adjacentExons = new Exons[2];
/*  22 */   String to_print = "";
/*     */   Transcript transcript;
/*  24 */   String id = "";
/*     */   
/*     */   public MappedAnchors(int leftstart, int rightstart, Transcript t) {
/*  27 */     this.transcript = t;
/*     */     try {
/*  29 */       this.exons[0] = resolveExons(leftstart);
/*  30 */       this.exons[1] = resolveExons(rightstart);
/*     */       
/*     */ 
/*  33 */       determine_canonical_junctions();
/*     */     } catch (Exception e) {
/*  35 */       e.getMessage();
/*     */     }
/*     */   }
/*     */   
/*     */   public MappedAnchors(Reads left, Reads right) {}
/*     */   
/*     */   public MappedAnchors(Reads left, Reads right, Transcript transcript)
/*     */   {
/*  43 */     this.transcript = transcript;
/*     */     try {
/*  45 */       this.exons[0] = resolveExons(left.getStart());
/*  46 */       this.exons[1] = resolveExons(right.getStart());
/*     */     }
/*     */     catch (Exception e) {
/*  49 */       e.getMessage();
/*     */     }
/*     */   }
/*     */   
/*     */   private Exons resolveExons(int start) {
/*  54 */     Exons temp = null;
/*     */     
/*     */ 
/*  57 */     for (Exons e : this.transcript.getExons().values()) {
/*  58 */       if (e.getRange().contains(Integer.valueOf(start)))
/*     */       {
/*  60 */         temp = e;
/*     */         
/*  62 */         break;
/*     */       }
/*     */     }
/*     */     
/*  66 */     return temp;
/*     */   }
/*     */   
/*     */   public boolean checkJunction() {
/*  70 */     if ((this.exons[0] != null) && (this.exons[1] != null) && (this.exons[0].getOrder() >= this.exons[1].getOrder()))
/*     */     {
/*     */ 
/*  73 */       this.id = (this.transcript.getRefseq() + "." + this.exons[0].getOrder() + "." + this.exons[1].getOrder());
/*  74 */       this.junctionSpan = true;
/*  75 */       this.to_print = (this.transcript.getRefseq() + "\t" + this.transcript.getRefseq() + "\t" + this.id + "\t" + this.exons[0].getStart() + "\t" + this.exons[0].getStop() + "\t" + this.exons[1].getStart() + "\t" + this.exons[1].getStop() + "\t" + this.to_print);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  80 */     return this.junctionSpan;
/*     */   }
/*     */   
/*     */   private void determine_adjacent_exons() {
/*  84 */     int prime5exon = getExons()[0].getOrder() > this.transcript.getNumOfExons() - 1 ? getExons()[0].getOrder() : getExons()[0].getOrder() + 1;
/*  85 */     int prime3exon = getExons()[1].getOrder() < 1 ? getExons()[1].getOrder() : getExons()[1].getOrder() - 1;
/*     */     
/*  87 */     Exons t5 = (Exons)this.transcript.getExons().get(Integer.valueOf(prime5exon));
/*  88 */     Exons t3 = (Exons)this.transcript.getExons().get(Integer.valueOf(prime3exon));
/*  89 */     this.adjacentExons[0] = t5;
/*  90 */     this.adjacentExons[1] = t3;
/*  91 */     String right_can = this.transcript.getRefseq() + "." + getExons()[0].getOrder() + "." + t5.getOrder() + "\t" + getExons()[0].getStart() + "\t" + getExons()[0].getStop() + "\t" + t5.getStart() + "\t" + t5.getStop();
/*     */     
/*     */ 
/*  94 */     String left_can = this.transcript.getRefseq() + "." + t3.getOrder() + "." + getExons()[1].getOrder() + "\t" + t3.getStart() + "\t" + t3.getStop() + "\t" + getExons()[1].getStart() + "\t" + getExons()[1].getStop();
/*     */     
/*     */ 
/*  97 */     this.to_print = (left_can + "\t" + right_can);
/*     */   }
/*     */   
/*     */   private void determine_canonical_junctions()
/*     */   {
/* 102 */     String temp = "";
/* 103 */     for (Exons e : this.transcript.getExons().values()) {
/* 104 */       int x = e.getOrder();
/* 105 */       int y = x - 1;
/*     */       
/*     */ 
/* 108 */       if ((x <= this.transcript.getNumOfExons()) && (y > 0)) {
/* 109 */         Exons e1 = (Exons)this.transcript.getExons().get(Integer.valueOf(y));
/* 110 */         temp = temp + this.transcript.getRefseq() + "." + e1.getOrder() + "." + e.getOrder() + "\t" + e1.getStart() + "\t" + e1.getStop() + "\t" + e.getStart() + "\t" + e.getStop() + "\t";
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 117 */     this.to_print = temp;
/*     */   }
/*     */   
/*     */   public Exons[] getExons() {
/* 121 */     return this.exons;
/*     */   }
/*     */   
/*     */   public void setExons(Exons[] exons) {
/* 125 */     this.exons = exons;
/*     */   }
/*     */   
/*     */   public Transcript getTranscript() {
/* 129 */     return this.transcript;
/*     */   }
/*     */   
/*     */   public void setTranscript(Transcript transcript) {
/* 133 */     this.transcript = transcript;
/*     */   }
/*     */   
/*     */   public String getId() {
/* 137 */     return this.id;
/*     */   }
/*     */   
/*     */   public boolean isJunctionSpan() {
/* 141 */     return this.junctionSpan;
/*     */   }
/*     */   
/*     */   public void setJunctionSpan(boolean junctionSpan) {
/* 145 */     this.junctionSpan = junctionSpan;
/*     */   }
/*     */   
/*     */   public String getTo_print() {
/* 149 */     return this.to_print;
/*     */   }
/*     */   
/*     */   public void setTo_print(String to_print) {
/* 153 */     this.to_print = to_print;
/*     */   }
/*     */   
/*     */   public Exons[] getAdjacentExons() {
/* 157 */     return this.adjacentExons;
/*     */   }
/*     */   
/*     */   public void setAdjacentExons(Exons[] adjacentExons) {
/* 161 */     this.adjacentExons = adjacentExons;
/*     */   }
/*     */ }

