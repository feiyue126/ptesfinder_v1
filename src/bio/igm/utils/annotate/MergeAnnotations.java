/*     */ package bio.igm.utils.annotate;
/*     */ 
/*     */ import bio.igm.utils.init.Logging;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.math3.stat.StatUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MergeAnnotations
/*     */ {
/*     */   String path;
/*     */   String can_path;
/*     */   String ptes_path;
/*  32 */   Map<String, String> ptes = new HashMap();
/*  33 */   Map<String, List<String>> can_junctions = new HashMap();
/*  34 */   Map<String, String> output = new HashMap();
/*     */   public static Logger LOG;
/*     */   
/*     */   public MergeAnnotations(String _path, String _ptes, String _can) throws IOException {
/*  38 */     this.ptes_path = _ptes;
/*  39 */     this.can_path = _can;
/*  40 */     this.path = _path;
/*  41 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  44 */       if (f.isDirectory()) {
/*  45 */         LOG = new Logging(_path, AnnotateStructures.class.getName()).setup();
/*     */       } else {
/*  47 */         LOG = new Logging(f.getParent(), AnnotateStructures.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  50 */       Logger.getLogger(AnnotateStructures.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*  53 */     LOG.info("Reading input bed files..");
/*  54 */     read_ptes_bed();
/*  55 */     read_canonical_bed();
/*  56 */     LOG.info("Appending canonical junction counts to each discovered PTES structure..");
/*  57 */     append_can_counts();
/*  58 */     writeToFile();
/*  59 */     LOG.info("Finished merging files - see final output file: annotated-ptes.bed ");
/*     */   }
/*     */   
/*     */   private void read_ptes_bed() throws IOException {
/*  63 */     BufferedReader br = new BufferedReader(new FileReader(this.ptes_path));
/*  64 */     String line = "";
/*  65 */     while ((line = br.readLine()) != null) {
/*  66 */       this.ptes.put(line.split("\t")[3], line);
/*     */     }
/*  68 */     br.close();
/*     */   }
/*     */   
/*     */   private void read_canonical_bed() throws IOException
/*     */   {
/*  73 */     BufferedReader br = new BufferedReader(new FileReader(this.can_path));
/*  74 */     String line = "";
/*  75 */     while ((line = br.readLine()) != null) {
/*  76 */       String refseq = line.split("\t")[0].split("\\.")[0];
/*  77 */       if (this.can_junctions.containsKey(refseq)) {
/*  78 */         ((List)this.can_junctions.get(refseq)).add(line);
/*     */       } else {
/*  80 */         List<String> temp = new ArrayList();
/*  81 */         temp.add(line);
/*  82 */         this.can_junctions.put(refseq, temp);
/*     */       }
/*     */     }
/*  85 */     br.close();
/*     */   }
/*     */   
/*     */   private void append_can_counts() {
/*  89 */     for (String s : this.ptes.keySet()) {
/*  90 */       String structure = (String)this.ptes.get(s);
/*  91 */       String refseq = s.split("\\.")[0];
/*  92 */       int prime5 = Integer.parseInt(s.split("\\.")[1]);
/*  93 */       int prime3 = Integer.parseInt(s.split("\\.")[2]);
/*  94 */       String flank5 = refseq + "." + prime5 + "." + (prime5 + 1);
/*  95 */       String flank3 = refseq + "." + (prime3 - 1) + "." + prime3;
/*     */       
/*  97 */       double max_can_count = 0.0D;
/*  98 */       double mean_can_count = 0.0D;
/*  99 */       double sum_can_count = 0.0D;
/* 100 */       double mean_flanking_count = 0.0D;
/* 101 */       double sum_flanking_count = 0.0D;
/*     */       
/*     */ 
/* 104 */       String expression = "NA";
/*     */       
/* 106 */       if (this.can_junctions.containsKey(refseq)) {
/* 107 */         double[] counts = new double[((List)this.can_junctions.get(refseq)).size()];
/* 108 */         int[] index = new int[((List)this.can_junctions.get(refseq)).size()];
/* 109 */         Map<Integer, Double> all_counts = new HashMap();
/* 110 */         int counter = 0;
/*     */         
/* 112 */         for (String canonical : (List)this.can_junctions.get(refseq)) {
/* 113 */           int i = Integer.parseInt(canonical.split("\t")[0].split("\\.")[1]);
/* 114 */           int x = Integer.parseInt(canonical.split("\t")[0].split("\\.")[2]);
/*     */           
/* 116 */           int z = x - i > 1 ? i * 1000 + x - 1 : i;
/*     */           
/* 118 */           all_counts.put(Integer.valueOf(z), Double.valueOf(Integer.parseInt(canonical.split("\t")[2])));
/* 119 */           if ((canonical.split("\t")[0].equalsIgnoreCase(flank5)) || (canonical.split("\t")[0].equalsIgnoreCase(flank3))) {
/* 120 */             sum_flanking_count += Integer.parseInt(canonical.split("\t")[2]);
/*     */           }
/*     */           
/* 123 */           index[counter] = z;
/*     */           
/* 125 */           counter++;
/*     */         }
/* 127 */         mean_flanking_count = sum_flanking_count / 2.0D;
/*     */         
/* 129 */         counter = 0;
/* 130 */         Arrays.sort(index);
/* 131 */         for (int x : index) {
/* 132 */           counts[counter] = ((Double)all_counts.get(Integer.valueOf(x))).doubleValue();
/* 133 */           counter++;
/*     */         }
/* 135 */         if (counts.length > 0) {
/* 136 */           expression = "";
/* 137 */           for (int i = 0; i < counts.length; i++) {
/* 138 */             int $5 = index[i] / 1000 > 0 ? index[i] / 1000 : index[i];
/* 139 */             expression = expression + $5 + "." + (index[i] % 1000 + 1) + "_" + counts[i] + "|";
/*     */           }
/*     */         } else {
/* 142 */           expression = "NA";
/*     */         }
/*     */         
/* 145 */         max_can_count = StatUtils.max(counts);
/* 146 */         mean_can_count = StatUtils.mean(counts);
/* 147 */         sum_can_count = StatUtils.sum(counts);
/*     */       }
/*     */       
/* 150 */       String out = mean_flanking_count + "\t" + sum_flanking_count + "\t" + max_can_count + "\t" + mean_can_count + "\t" + sum_can_count + "\t" + expression;
/* 151 */       this.output.put(s, structure + "\t" + out);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException {
/* 156 */     String header = "#chromosome\tstart\tstop\tstructure\traw_count\tstrand\tmean_flanking_can_count\tsum_flanking_can_count\tmax_can_count\tmean_can_count\tsum_can_count\tcanonical_expression";
/*     */     
/* 158 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "annotated-ptes.bed"));
/* 159 */     bw.write(header + "\n");
/* 160 */     for (String s : this.output.values()) {
/* 161 */       bw.write(s + "\n");
/*     */     }
/*     */     
/* 164 */     bw.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try {
/* 170 */       new MergeAnnotations(args[0], args[1], args[2]);
/*     */     } catch (IOException ex) {
/* 172 */       Logger.getLogger(MergeAnnotations.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
