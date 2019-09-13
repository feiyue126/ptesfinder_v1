/*     */ package bio.igm.utils.filter;
/*     */ 
/*     */ import bio.igm.entities.Reads;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilterTranscriptomicHits
/*     */ {
/*     */   private String path;
/*     */   private Map<String, Reads> reads;
/*     */   
/*     */   FilterTranscriptomicHits(Map<String, Reads> reads, String _path)
/*     */     throws IOException
/*     */   {
/*  28 */     this.path = _path;
/*     */     
/*  30 */     this.reads = reads;
/*  31 */     PipelineFilter.LOG.info("Comparing reads aligned to constructs to reads aligned to transcriptomic reference..");
/*  32 */     filter_refseq_reads();
/*     */   }
/*     */   
/*     */   private void filter_refseq_reads() throws IOException {
/*  36 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "refseq.sam"));
/*  37 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "refseq-filtered-out.sam"));
/*  38 */     BufferedWriter bw2 = new BufferedWriter(new FileWriter(this.path + "refseq-better.sam"));
/*  39 */     BufferedWriter bp = new BufferedWriter(new FileWriter(this.path + "refunique.sam"));
/*  40 */     String line = "";
/*  41 */     String id = "";
/*  42 */     Reads r = null;
/*     */     
/*  44 */     while ((line = br.readLine()) != null) {
/*  45 */       if (line.split("\t").length > 13) {
/*  46 */         id = line.split("\t")[0];
/*  47 */         if (this.reads.containsKey(id)) {
/*  48 */           r = (Reads)this.reads.get(id);
/*  49 */           if (!compareNMs(r.getLine(), line)) {
/*  50 */             bw.write(r.getLine() + "\n");
/*  51 */             bw2.write(line + "\n");
/*  52 */             this.reads.remove(r.getId());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  57 */     for (Reads p : this.reads.values())
/*     */     {
/*  59 */       bp.write(p.getLine() + "\n");
/*     */     }
/*     */     
/*  62 */     br.close();
/*  63 */     bw.close();
/*  64 */     bw2.close();
/*  65 */     bp.close();
/*     */   }
/*     */   
/*     */   private boolean compareNMs(String ptes_read, String refseq_read) {
/*  69 */     boolean better = false;
/*     */     
/*  71 */     int x = extract_aligned(ptes_read.split("\t")[(ptes_read.split("\t").length - 2)]);
/*  72 */     int y = extract_aligned(refseq_read.split("\t")[(refseq_read.split("\t").length - 2)]);
/*  73 */     int ptes_nm = Integer.parseInt(ptes_read.split("\t")[(ptes_read.split("\t").length - 3)].split(":")[2]);
/*  74 */     int refseq_nm = Integer.parseInt(refseq_read.split("\t")[(refseq_read.split("\t").length - 3)].split(":")[2]);
/*     */     
/*  76 */     if ((x >= y) && (ptes_nm < refseq_nm)) {
/*  77 */       better = true;
/*     */     }
/*  79 */     return better;
/*     */   }
/*     */   
/*     */   private int extract_aligned(String MD) {
/*  83 */     String pattern = "[0-9]+";
/*  84 */     int temp = 0;
/*  85 */     Pattern p = Pattern.compile(pattern);
/*  86 */     Matcher m = p.matcher(MD.split(":")[2]);
/*     */     
/*  88 */     while (m.find()) {
/*  89 */       temp += Integer.parseInt(m.group());
/*     */     }
/*  91 */     return temp;
/*     */   }
/*     */   
/*     */   public Map<String, Reads> getReads() {
/*  95 */     return this.reads;
/*     */   }
/*     */   
/*     */   public void setReads(Map<String, Reads> reads) {
/*  99 */     this.reads = reads;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 103 */     return this.path;
/*     */   }
/*     */   
/*     */   public void setPath(String path) {
/* 107 */     this.path = path;
/*     */   }
/*     */ }


