/*     */ package bio.igm.utils.filter;
/*     */ 
/*     */ import bio.igm.entities.PTES;
/*     */ import bio.igm.entities.Reads;
/*     */ import bio.igm.utils.init.Logging;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class PipelineFilter
/*     */ {
/*  26 */   int jspan = 0;
/*  27 */   double pid = 0.0D;
/*     */   
/*  29 */   Map<String, PTES> putative_ptes = new HashMap();
/*  30 */   Map<String, PTES> canonical = new HashMap();
/*     */   
/*  32 */   Map<String, Reads> raw_ptes_reads = new HashMap();
/*  33 */   Map<String, Reads> processed_ptes_reads = new HashMap();
/*     */   
/*  35 */   Map<String, Reads> raw_canonical_reads = new HashMap();
/*  36 */   Map<String, Reads> processed_canonical_reads = new HashMap();
/*     */   
/*  38 */   Map<String, String> constructs = new HashMap();
/*     */   
/*     */   String path;
/*  41 */   boolean filters = true;
/*  42 */   boolean refseq = true;
/*  43 */   boolean genomic = true;
/*     */   public static Logger LOG;
/*     */   
/*     */   public PipelineFilter(String _path, int _jspan, double _pid, boolean _filters, boolean _genomic, boolean _refseq) throws IOException {
/*  47 */     this.path = _path;
/*  48 */     this.filters = _filters;
/*  49 */     this.pid = _pid;
/*  50 */     this.jspan = _jspan;
/*  51 */     this.filters = _filters;
/*  52 */     this.refseq = _refseq;
/*  53 */     this.genomic = _genomic;
/*  54 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  57 */       if (f.isDirectory()) {
/*  58 */         LOG = new Logging(_path, PipelineFilter.class.getName()).setup();
/*     */       } else {
/*  60 */         LOG = new Logging(f.getParent(), PipelineFilter.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  63 */       Logger.getLogger(PipelineFilter.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*  65 */     LOG.info("Reading ptes.sam and applying filters..\nInput Parameters:\n\tPID -> " + _pid + "\n\tJSpan -> " + _jspan);
/*     */     
/*  67 */     this.raw_ptes_reads = get_sam_reads("ptes");
/*     */     
/*     */ 
/*  70 */     if (this.filters) {
/*  71 */       this.refseq = false;
/*  72 */       this.genomic = false;
/*  73 */       this.processed_ptes_reads = apply_filters("ptes", this.raw_ptes_reads);
/*     */     }
/*  75 */     else if ((this.genomic) || (this.refseq)) {
/*  76 */       this.processed_ptes_reads = apply_filters("ptes", this.raw_ptes_reads);
/*     */     }
/*     */     else
/*     */     {
/*  80 */       this.processed_ptes_reads = get_sam_reads("ptes");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  85 */     this.raw_canonical_reads = null;
/*  86 */     this.raw_ptes_reads = null;
/*     */     
/*  88 */     System.gc();
/*     */     
/*  90 */     LOG.info("Applying Minimum Junction Span and Construct Percent Identity Filters .. ");
/*  91 */     this.putative_ptes = new MDFilter(this.processed_ptes_reads, this.jspan, this.pid, this.path).getPtes();
/*     */     
/*  93 */     this.processed_ptes_reads = null;
/*  94 */     System.gc();
/*     */     
/*     */ 
/*  97 */     LOG.info("Processing reads mapped to Canonical Junctions ..");
/*  98 */     this.canonical = new MDFilter(this.path, this.jspan, this.pid).getPtes();
/*  99 */     this.processed_canonical_reads = null;
/*     */   }
/*     */   
/*     */   private Map<String, Reads> get_sam_reads(String filename)
/*     */     throws IOException
/*     */   {
/* 105 */     Map<String, Reads> temp = new HashMap();
/* 106 */     BufferedReader br = new BufferedReader(new FileReader(this.path + filename + ".sam"));
/*     */     
/* 108 */     String line = "";
/*     */     
/* 110 */     while ((line = br.readLine()) != null) {
/* 111 */       Reads r = new Reads(line);
/* 112 */       temp.put(r.getId(), r);
/*     */     }
/*     */     
/* 115 */     return temp;
/*     */   }
/*     */   
/*     */   private Map<String, Reads> apply_filters(String type, Map<String, Reads> _structures) throws IOException {
/* 119 */     Map<String, Reads> temp = _structures;
/*     */     
/* 121 */     if (type.equalsIgnoreCase("ptes")) {
/* 122 */       if (this.genomic)
/*     */       {
/* 124 */         LOG.info("[ Applying Genomic Filter Only.. ");
/* 125 */         temp = new FilterGenomicHits(this.path).getReads();
/* 126 */       } else { if (this.refseq) {
/* 127 */           LOG.info("Applying Transcriptomic Filter Only.. ");
/* 128 */           return new FilterTranscriptomicHits(temp, this.path).getReads();
/*     */         }
/* 130 */         LOG.info("Applying Genomic & Transcriptomic Filters ..");
/* 131 */         return new FilterTranscriptomicHits(new FilterGenomicHits(this.path).getReads(), this.path).getReads();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 136 */     return temp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 143 */     String path = args[0];
/* 144 */     int _jspan = 8;
/* 145 */     double _pid = 0.85D;
/* 146 */     boolean all_filters = true;
/* 147 */     boolean genomic = true;
/* 148 */     boolean refseq = true;
/*     */     
/*     */     try
/*     */     {
/* 152 */       _jspan = Integer.parseInt(args[1]);
/* 153 */       _pid = Double.parseDouble(args[2]);
/* 154 */       all_filters = !args[3].equalsIgnoreCase("0");
/* 155 */       genomic = !args[4].equalsIgnoreCase("0");
/* 156 */       refseq = !args[5].equalsIgnoreCase("0");
/*     */     } catch (Exception e) {
/* 158 */       LOG.severe("\nThere are errors in your input parameters. Please re-check before running again.\nProceeding with default values:\n>>> Run all filters = Yes\n\t PID = 0.85 \n\t Junction Span = 8\n\n");
/*     */     }
/*     */     try
/*     */     {
/* 162 */       new PipelineFilter(path, _jspan, _pid, all_filters, genomic, refseq);
/*     */     } catch (IOException ex) {
/* 164 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
