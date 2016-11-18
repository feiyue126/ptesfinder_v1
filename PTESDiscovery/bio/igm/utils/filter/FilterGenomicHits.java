/*     */ package bio.igm.utils.filter;
/*     */ 
/*     */ import bio.igm.entities.Reads;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ public class FilterGenomicHits
/*     */ {
/*  24 */   Map<String, Reads> reads = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   String path;
/*     */   
/*     */ 
/*     */ 
/*     */   public FilterGenomicHits(String _path)
/*     */     throws IOException
/*     */   {
/*  35 */     this.path = _path;
/*     */     
/*  37 */     read_ptes_sam();
/*  38 */     PipelineFilter.LOG.info("Comparing reads aligned to constructs to reads aligned to genomic reference..");
/*  39 */     filter_genomic_reads();
/*     */   }
/*     */   
/*     */ 
/*     */   private void read_ptes_sam()
/*     */     throws IOException
/*     */   {
/*  46 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "ptes.sam"));
/*  47 */     String line = "";
/*     */     
/*  49 */     while ((line = br.readLine()) != null)
/*     */     {
/*  51 */       if (line.split("\t").length > 13) {
/*  52 */         this.reads.put(line.split("\t")[0], new Reads(line));
/*     */       }
/*     */     }
/*  55 */     br.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void filter_genomic_reads()
/*     */     throws IOException
/*     */   {
/*  64 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "genomic.sam"));
/*  65 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "genomic-filtered-out.sam"));
/*  66 */     BufferedWriter bw2 = new BufferedWriter(new FileWriter(this.path + "genomic-better.sam"));
/*  67 */     BufferedWriter bp = new BufferedWriter(new FileWriter(this.path + "unique.sam"));
/*  68 */     String line = "";
/*  69 */     String id = "";
/*  70 */     Reads r = null;
/*     */     
/*  72 */     while ((line = br.readLine()) != null) {
/*  73 */       if (line.split("\t").length > 13) {
/*  74 */         id = line.split("\t")[0];
/*  75 */         if (this.reads.containsKey(id)) {
/*  76 */           r = (Reads)this.reads.get(id);
/*  77 */           if (!compareNMs(r.getLine(), line)) {
/*  78 */             bw.write(r.getLine() + "\n");
/*  79 */             bw2.write(line + "\n");
/*  80 */             this.reads.remove(r.getId());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  86 */     for (Reads p : this.reads.values())
/*     */     {
/*  88 */       bp.write(p.getLine() + "\n");
/*     */     }
/*     */     
/*  91 */     br.close();
/*  92 */     bw.close();
/*  93 */     bw2.close();
/*  94 */     bp.close();
/*     */   }
/*     */   
/*     */   private boolean compareNMs(String ptes_read, String genomic_read) {
/*  98 */     boolean better = false;
/*     */     
/* 100 */     int x = extract_aligned(ptes_read.split("\t")[(ptes_read.split("\t").length - 2)]);
/* 101 */     int y = extract_aligned(genomic_read.split("\t")[(genomic_read.split("\t").length - 2)]);
/* 102 */     int ptes_nm = Integer.parseInt(ptes_read.split("\t")[(ptes_read.split("\t").length - 3)].split(":")[2]);
/* 103 */     int genomic_nm = Integer.parseInt(genomic_read.split("\t")[(genomic_read.split("\t").length - 3)].split(":")[2]);
/*     */     
/*     */ 
/* 106 */     if ((x >= y) && (ptes_nm < genomic_nm)) {
/* 107 */       better = true;
/*     */     }
/* 109 */     return better;
/*     */   }
/*     */   
/*     */   private int extract_aligned(String MD)
/*     */   {
/* 114 */     String pattern = "[0-9]+";
/* 115 */     int temp = 0;
/* 116 */     Pattern p = Pattern.compile(pattern);
/* 117 */     Matcher m = p.matcher(MD.split(":")[2]);
/*     */     
/* 119 */     while (m.find()) {
/* 120 */       temp += Integer.parseInt(m.group());
/*     */     }
/* 122 */     return temp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Reads> getReads()
/*     */   {
/* 130 */     return this.reads;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReads(Map<String, Reads> reads)
/*     */   {
/* 138 */     this.reads = reads;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 146 */     return this.path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPath(String path)
/*     */   {
/* 154 */     this.path = path;
/*     */   }
/*     */ }
