/*     */ package bio.igm.entities;
/*     */ 
/*     */ import org.apache.commons.lang3.Range;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Exons
/*     */ {
/*  14 */   int order = -1;
/*  15 */   int start = -1;
/*  16 */   int stop = -1;
/*     */   String refid;
/*     */   Transcript transcript;
/*     */   Range<Integer> range;
/*  20 */   String sequence = "";
/*  21 */   int size = 0;
/*     */   
/*     */   public Exons(Transcript rna, int start, int stop) {
/*  24 */     this.refid = rna.getRefseq();
/*  25 */     this.transcript = rna;
/*  26 */     this.start = start;
/*  27 */     this.stop = stop;
/*  28 */     this.size = (stop - start);
/*     */   }
/*     */   
/*  31 */   public Exons(String id, String seq) { if ((id.startsWith("NM")) || (id.startsWith("NR"))) {
/*  32 */       this.refid = (id.split("_")[0] + "_" + id.split("_")[1]);
/*  33 */       this.order = Integer.parseInt(id.split("_")[2]);
/*     */     } else {
/*  35 */       this.refid = id.split("_")[0].replace(".", ":");
/*  36 */       this.order = Integer.parseInt(id.split("_")[1]);
/*     */     }
/*     */     
/*  39 */     this.sequence = seq;
/*  40 */     this.size = seq.length();
/*     */   }
/*     */   
/*     */   public int getOrder()
/*     */   {
/*  45 */     return this.order;
/*     */   }
/*     */   
/*     */   public void setOrder(int order) {
/*  49 */     this.order = order;
/*     */   }
/*     */   
/*     */   public Range<Integer> getRange() {
/*  53 */     this.range = Range.between(Integer.valueOf(getStart()), Integer.valueOf(getStop()));
/*  54 */     return this.range;
/*     */   }
/*     */   
/*     */   public void setRange(Range<Integer> range) {
/*  58 */     this.range = range;
/*     */   }
/*     */   
/*     */   public int getSize() {
/*  62 */     return this.size;
/*     */   }
/*     */   
/*     */   public void setSize(int size) {
/*  66 */     this.size = size;
/*     */   }
/*     */   
/*     */   public int getStart() {
/*  70 */     return this.start;
/*     */   }
/*     */   
/*     */   public void setStart(int start) {
/*  74 */     this.start = start;
/*     */   }
/*     */   
/*     */   public int getStop() {
/*  78 */     return this.stop;
/*     */   }
/*     */   
/*     */   public void setStop(int stop) {
/*  82 */     this.stop = stop;
/*     */   }
/*     */   
/*     */   public String getRefid() {
/*  86 */     return this.refid;
/*     */   }
/*     */   
/*     */   public void setRefid(String refid) {
/*  90 */     this.refid = refid;
/*     */   }
/*     */   
/*     */   public Transcript getTranscript() {
/*  94 */     return this.transcript;
/*     */   }
/*     */   
/*     */   public void setTranscript(Transcript transcript) {
/*  98 */     this.transcript = transcript;
/*     */   }
/*     */   
/*     */   public String getSequence() {
/* 102 */     return this.sequence;
/*     */   }
/*     */   
/*     */   public void setSequence(String sequence) {
/* 106 */     this.sequence = sequence;
/*     */   }
/*     */ }
