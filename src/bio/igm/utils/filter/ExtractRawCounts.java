/*     */ package bio.igm.utils.filter;
/*     */ 
/*     */ import bio.igm.entities.Reads;
/*     */ import bio.igm.utils.init.Logging;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
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
/*     */ 
/*     */ public class ExtractRawCounts
/*     */ {
/*     */   String path;
/*  29 */   Map<String, Integer> ptes_counts = new HashMap();
/*  30 */   Map<String, Integer> canonical_counts = new HashMap();
/*     */   private static Logger LOG;
/*     */   
/*     */   public ExtractRawCounts(String _path) throws IOException
/*     */   {
/*  35 */     this.path = _path;
/*     */     
/*  37 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  40 */       if (f.isDirectory()) {
/*  41 */         LOG = new Logging(_path, ExtractRawCounts.class.getName()).setup();
/*     */       } else {
/*  43 */         LOG = new Logging(f.getParent(), ExtractRawCounts.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  46 */       Logger.getLogger(ExtractRawCounts.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*  49 */     LOG.info("Generating raw counts from ptes.sam .. ");
/*     */     
/*  51 */     this.ptes_counts = extract_counts_from_sam("ptes.sam");
/*  52 */     LOG.info("Generating raw counts from canonical.sam .. ");
/*     */     
/*  54 */     this.canonical_counts = extract_counts_from_sam("canonical.sam");
/*     */     
/*  56 */     writeToFile();
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException {
/*  60 */     BufferedWriter bwP = new BufferedWriter(new FileWriter(this.path + "ptes-raw.counts"));
/*  61 */     BufferedWriter bwC = new BufferedWriter(new FileWriter(this.path + "canonical-raw.counts"));
/*     */     
/*     */ 
/*  64 */     for (String s : this.ptes_counts.keySet()) {
/*  65 */       bwP.write(s + "\t" + this.ptes_counts.get(s) + "\n");
/*     */     }
/*  67 */     for (String s : this.canonical_counts.keySet()) {
/*  68 */       bwC.write(s + "\t" + this.canonical_counts.get(s) + "\n");
/*     */     }
/*     */     
/*  71 */     bwP.close();
/*  72 */     bwC.close();
/*     */   }
/*     */   
/*     */   private Map<String, Integer> extract_counts_from_sam(String filename)
/*     */     throws IOException
/*     */   {
/*  78 */     Map<String, Integer> temp = new HashMap();
/*  79 */     BufferedReader br = new BufferedReader(new FileReader(this.path + filename));
/*     */     
/*  81 */     String line = "";
/*     */     
/*  83 */     while ((line = br.readLine()) != null) {
/*  84 */       Reads r = new Reads(line);
/*  85 */       if (temp.containsKey(r.getTarget())) {
/*  86 */         int x = ((Integer)temp.get(r.getTarget())).intValue() + 1;
/*  87 */         temp.put(r.getTarget(), Integer.valueOf(x));
/*     */       } else {
/*  89 */         temp.put(r.getTarget(), Integer.valueOf(1));
/*     */       }
/*     */     }
/*  92 */     return temp;
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*  97 */     String path = args[0];
/*     */     try {
/*  99 */       new ExtractRawCounts(path);
/*     */     }
/*     */     catch (IOException ex) {
/* 102 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
