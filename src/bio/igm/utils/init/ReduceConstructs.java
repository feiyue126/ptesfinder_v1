/*     */ package bio.igm.utils.init;
/*     */ 
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
/*     */ import org.apache.commons.lang3.StringUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReduceConstructs
/*     */ {
/*     */   String path;
/*  33 */   Map<String, String> constructs = new HashMap();
/*  34 */   Map<String, String> can_constructs = new HashMap();
/*  35 */   int segment_size = 65;
/*     */   private static Logger LOG;
/*     */   
/*     */   public ReduceConstructs(String _path, int _segment_size) throws IOException {
/*  39 */     this.path = _path;
/*  40 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  43 */       if (f.isDirectory()) {
/*  44 */         LOG = new Logging(_path, ReduceConstructs.class.getName()).setup();
/*     */       } else {
/*  46 */         LOG = new Logging(f.getParent(), ReduceConstructs.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  49 */       Logger.getLogger(ReduceConstructs.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*  51 */     this.segment_size = _segment_size;
/*  52 */     LOG.info("Reading supplied reference FASTA files..");
/*  53 */     this.constructs = readConstructs("tempConstructs.fa");
/*  54 */     this.can_constructs = readConstructs("tempCan.fa");
/*     */     
/*  56 */     LOG.info("Shrinking constructs to specified segment length..");
/*  57 */     reduce_constructs("ptes");
/*  58 */     reduce_constructs("canonical");
/*     */   }
/*     */   
/*     */   public ReduceConstructs(String _path, String _ptes_path, String _can_path, int _segment_size) throws IOException {
/*  62 */     this.path = _path;
/*  63 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  66 */       if (f.isDirectory()) {
/*  67 */         LOG = new Logging(_path, ReduceConstructs.class.getName()).setup();
/*     */       } else {
/*  69 */         LOG = new Logging(f.getParent(), ReduceConstructs.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  72 */       Logger.getLogger(ReduceConstructs.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*  74 */     this.segment_size = _segment_size;
/*  75 */     LOG.info("Reading supplied reference FASTA files..");
/*  76 */     File p = new File(_ptes_path);
/*  77 */     if (p.exists()) {
/*  78 */       this.constructs = readConstructs(_ptes_path);
/*     */     } else {
/*  80 */       LOG.info("No PTES FASTA reference supplied - running unguided analysis..");
/*     */     }
/*  82 */     File c = new File(_can_path);
/*  83 */     if (c.exists()) {
/*  84 */       this.can_constructs = readConstructs(_can_path);
/*     */     } else {
/*  86 */       LOG.info("No Canonical Junctions FASTA reference supplied");
/*     */     }
/*     */     
/*     */ 
/*  90 */     LOG.info("Shrinking constructs to specified segment length..");
/*  91 */     reduce_constructs("ptes");
/*  92 */     reduce_constructs("canonical");
/*     */   }
/*     */   
/*     */   private Map<String, String> readConstructs(String filename) throws IOException {
/*  96 */     Map<String, String> temp = new HashMap();
/*     */     
/*  98 */     BufferedReader br = new BufferedReader(new FileReader(filename));
/*  99 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 101 */     String line = "";
/*     */     
/*     */ 
/* 104 */     while ((line = br.readLine()) != null) {
/* 105 */       builder.append(line);
/* 106 */       builder.append(System.getProperty("line.separator"));
/*     */     }
/*     */     
/*     */ 
/* 110 */     String[] seqs = builder.toString().split(">");
/* 111 */     String seq = "";
/*     */     
/* 113 */     for (String s : seqs) {
/* 114 */       if (StringUtils.isNotBlank(s)) {
/* 115 */         String[] oneSeq = s.trim().split("\n");
/* 116 */         String id = oneSeq[0];
/*     */         
/* 118 */         seq = StringUtils.join(oneSeq, "", 1, oneSeq.length);
/* 119 */         temp.put(id, seq);
/*     */       }
/*     */     }
/*     */     
/* 123 */     br.close();
/* 124 */     return temp;
/*     */   }
/*     */   
/*     */   private void reduce_constructs(String structure) throws IOException {
/* 128 */     String filename = "";
/* 129 */     Map<String, String> temp_in = new HashMap();
/* 130 */     Map<String, String> temp_out = new HashMap();
/*     */     
/* 132 */     if (structure.equalsIgnoreCase("ptes")) {
/* 133 */       filename = "tempConstructs.fasta";
/* 134 */       temp_in = this.constructs;
/*     */     } else {
/* 136 */       filename = "tempCan.fasta";
/* 137 */       temp_in = this.can_constructs;
/*     */     }
/* 139 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + filename));
/* 140 */     for (String s : temp_in.keySet()) {
/* 141 */       int junction = Integer.parseInt(s.split("\\.")[3]);
/* 142 */       String[] seq = trim_seq((String)temp_in.get(s), junction);
/* 143 */       String id = s.split("\\.")[0] + "." + s.split("\\.")[1] + "." + s.split("\\.")[2] + "." + seq[1];
/* 144 */       bw.write(">" + id + "\n" + seq[0] + "\n");
/*     */     }
/*     */     
/* 147 */     bw.close();
/*     */   }
/*     */   
/*     */   private String[] trim_seq(String sequence, int junction) {
/* 151 */     String[] temp = new String[2];
/*     */     
/*     */ 
/* 154 */     if (sequence.length() > junction) {
/* 155 */       String seq1 = sequence.substring(0, junction);
/* 156 */       String seq2 = sequence.substring(junction);
/* 157 */       int x1 = seq1.length() > this.segment_size ? this.segment_size : seq1.length();
/* 158 */       int x2 = seq2.length() > this.segment_size ? this.segment_size : seq2.length();
/* 159 */       String temp1 = seq1.substring(seq1.length() - x1);
/* 160 */       String temp2 = seq2.substring(0, x2);
/*     */       
/* 162 */       temp[0] = (temp1 + temp2);
/* 163 */       temp[1] = (x1 + "");
/* 164 */       return temp;
/*     */     }
/*     */     
/* 167 */     return new String[] { "", "" };
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 171 */     String path = args[0];
/* 172 */     String ppath = args[2];
/* 173 */     String cpath = args[3];
/* 174 */     int segementSize = 65;
/*     */     try
/*     */     {
/* 177 */       segementSize = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException nfe) {
/* 179 */       LOG.info("Error parsing input parameter, proceeding with default value ..");
/*     */     }
/*     */     try
/*     */     {
/* 183 */       new ReduceConstructs(path, ppath, cpath, segementSize);
/*     */     }
/*     */     catch (IOException ex) {
/* 186 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
