/*     */ package bio.igm.utils.discovery;
/*     */ 
/*     */ import bio.igm.entities.ExonConstructs;
/*     */ import bio.igm.utils.init.Logging;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.lang3.Range;
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
/*     */ @Deprecated
/*     */ public class ProcessPutativeFusions
/*     */ {
/*     */   String path;
/*     */   String transcripts_bed;
/*  32 */   Map<String, Map<String, String>> exons_bed = new HashMap();
/*  33 */   Map<String, String> exons = new HashMap();
/*  34 */   Map<String, String> transcripts = new HashMap();
/*  35 */   Map<String, String> transcripts_merged = new HashMap();
/*  36 */   Map<String, String> transcripts_locus = new HashMap();
/*  37 */   Map<String, ExonConstructs> constructs = new HashMap();
/*  38 */   int segment_size = 65;
/*  39 */   int padding = 200000;
/*     */   private static Logger LOG;
/*     */   
/*     */   public ProcessPutativeFusions(int _segment_size, String _transcripts, String _path) throws IOException {
/*  43 */     this.segment_size = _segment_size;
/*  44 */     this.path = _path;
/*  45 */     this.transcripts_bed = _transcripts;
/*     */     
/*  47 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  50 */       if (f.isDirectory()) {
/*  51 */         LOG = new Logging(_path, ResolvePTESExonCoordinates.class.getName()).setup();
/*     */       } else {
/*  53 */         LOG = new Logging(f.getParent(), ResolvePTESExonCoordinates.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  56 */       Logger.getLogger(ResolvePTESExonCoordinates.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*     */ 
/*  60 */     LOG.info("Processing putative_fused_transcripts file..");
/*     */     
/*  62 */     get_transcripts_bed();
/*  63 */     get_merged_transcripts_bed();
/*  64 */     get_exons_bed();
/*  65 */     get_exons_sequence();
/*     */     
/*     */ 
/*  68 */     LOG.info("Accepting additional models..\nResolving alignment positions to exons and generating constructsfor new models..");
/*     */     
/*     */ 
/*  71 */     process_putative_fused_transcripts();
/*  72 */     writeToFile();
/*     */     
/*  74 */     LOG.info("Finished processing putative_fused_transcripts file..");
/*     */   }
/*     */   
/*     */   private void get_transcripts_bed() throws IOException
/*     */   {
/*  79 */     BufferedReader br = new BufferedReader(new FileReader(this.transcripts_bed));
/*  80 */     String line = "";
/*     */     
/*  82 */     while ((line = br.readLine()) != null) {
/*  83 */       this.transcripts.put(line.split("\t")[3].replace(".", ":"), line);
/*     */     }
/*     */     
/*  86 */     br.close();
/*     */   }
/*     */   
/*     */   private void get_exons_bed() throws IOException {
/*  90 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "/exons.bed"));
/*  91 */     String line = "";
/*     */     
/*  93 */     while ((line = br.readLine()) != null) {
/*  94 */       String id = line.split("\t")[3].replace(".", ":");
/*  95 */       String order = line.split("\t")[4];
/*     */       
/*  97 */       if (this.exons_bed.containsKey(id)) {
/*  98 */         ((Map)this.exons_bed.get(id)).put(id + "_" + order, line);
/*     */       } else {
/* 100 */         Map<String, String> temp = new HashMap();
/* 101 */         temp.put(id + "_" + order, line);
/* 102 */         this.exons_bed.put(id, temp);
/*     */       }
/*     */     }
/*     */     
/* 106 */     br.close();
/*     */   }
/*     */   
/*     */   private void get_exons_sequence() throws IOException {
/* 110 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "/exons.fa"));
/* 111 */     String line = "";
/* 112 */     StringBuilder builder = new StringBuilder();
/* 113 */     while ((line = br.readLine()) != null) {
/* 114 */       builder.append(line);
/* 115 */       builder.append(System.getProperty("line.separator"));
/*     */     }
/*     */     
/* 118 */     br.close();
/* 119 */     String[] seqs = builder.toString().split(">");
/* 120 */     String seq = "";
/*     */     
/* 122 */     for (String s : seqs) {
/* 123 */       if (StringUtils.isNotBlank(s)) {
/* 124 */         String[] oneSeq = s.trim().split("\n");
/*     */         
/* 126 */         String id = oneSeq[0].replace(".", ":");
/* 127 */         seq = StringUtils.join(oneSeq, "", 1, oneSeq.length);
/* 128 */         this.exons.put(id, seq.toUpperCase());
/*     */       }
/*     */     }
/*     */     
/* 132 */     LOG.info("Finished loading exons\nSize: " + this.exons.size());
/*     */   }
/*     */   
/*     */   private void process_putative_fused_transcripts() throws IOException {
/* 136 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "/putative_fused_transcripts_reads.tsv"));
/* 137 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "/excluded_fused_transcripts_reads.tsv"));
/* 138 */     String line = "";
/*     */     
/* 140 */     while ((line = br.readLine()) != null)
/*     */     {
/* 142 */       String left = "";
/* 143 */       String right = "";
/*     */       
/* 145 */       if (this.transcripts.containsKey(line.split("\t")[2])) {
/* 146 */         left = (String)this.transcripts.get(line.split("\t")[2]);
/*     */       }
/* 148 */       if (this.transcripts.containsKey(line.split("\t")[7])) {
/* 149 */         right = (String)this.transcripts.get(line.split("\t")[7]);
/*     */       }
/*     */       
/* 152 */       int coord_l = get_genomic_coordinate(Integer.parseInt(line.split("\t")[3]), left);
/* 153 */       int coord_r = get_genomic_coordinate(Integer.parseInt(line.split("\t")[8]), right);
/*     */       
/* 155 */       boolean overlap = check_locus_overlap(line.split("\t")[2], line.split("\t")[7]);
/*     */       
/* 157 */       if (overlap) {
/* 158 */         int orientation = Integer.parseInt(line.split("\t")[1]) > 0 ? 1 : -1;
/*     */         
/* 160 */         if (check_shuffling(coord_l, coord_r, left, right, orientation)) {
/* 161 */           Map<String, String> temp = (Map)this.exons_bed.get(left.split("\t")[3].replace(".", ":"));
/* 162 */           String exon5 = resolve_coord_to_exon(coord_l, temp);
/*     */           
/* 164 */           temp = (Map)this.exons_bed.get(right.split("\t")[3].replace(".", ":"));
/* 165 */           String exon3 = resolve_coord_to_exon(coord_l, temp);
/*     */           
/* 167 */           String seq1 = (String)this.exons.get(exon5);
/* 168 */           String seq2 = (String)this.exons.get(exon3);
/*     */           
/* 170 */           String strand = orientation > 0 ? "-" : "+";
/* 171 */           String id = left.split("\t")[0] + ":" + Math.min(coord_l, coord_r) + "-" + Math.max(coord_l, coord_r) + "_" + strand + ":" + left.split("\t")[3] + ":" + right.split("\t")[3] + "." + exon5.split("_")[(exon5.split("_").length - 1)] + "." + exon3.split("_")[(exon3.split("_").length - 1)];
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 176 */             ExonConstructs construct = new ExonConstructs(id, seq1, seq2, this.segment_size);
/*     */             
/* 178 */             this.constructs.put(construct.getId(), construct);
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 185 */         bw.write(line + "\n");
/*     */       }
/*     */     }
/*     */     
/* 189 */     bw.close();
/* 190 */     br.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int get_genomic_coordinate(int _mapped_from, String isoform)
/*     */   {
/* 197 */     int start = Integer.parseInt(isoform.split("\t")[1]);
/* 198 */     return start + _mapped_from - 1;
/*     */   }
/*     */   
/*     */   private boolean check_shuffling(int coord_l, int coord_r, String left, String right, int orientation) {
/* 202 */     boolean temp = false;
/* 203 */     if (left.split("\t")[0].equalsIgnoreCase(right.split("\t")[0])) {
/* 204 */       switch (orientation) {
/*     */       case -1: 
/* 206 */         if (coord_r < coord_l) {
/* 207 */           temp = true;
/*     */         }
/*     */         
/*     */         break;
/*     */       case 1: 
/* 212 */         if (coord_l < coord_r) {
/* 213 */           temp = true;
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 219 */     return temp;
/*     */   }
/*     */   
/*     */   private String resolve_coord_to_exon(int coord, Map<String, String> _exons) {
/* 223 */     String temp = "";
/*     */     
/* 225 */     for (String s : _exons.keySet()) {
/* 226 */       String exon = (String)_exons.get(s);
/* 227 */       Range<Integer> r = Range.between(Integer.valueOf(Integer.parseInt(exon.split("\t")[1])), Integer.valueOf(Integer.parseInt(exon.split("\t")[2])));
/*     */       
/* 229 */       if (r.contains(Integer.valueOf(coord))) {
/* 230 */         temp = s;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 235 */     return temp;
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException {
/* 239 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "/PTESJoints.fasta", true));
/* 240 */     System.out.println(this.constructs.size());
/* 241 */     for (String s : this.constructs.keySet()) {
/* 242 */       bw.write(">" + s + "\n" + ((ExonConstructs)this.constructs.get(s)).getSequence() + "\n");
/*     */     }
/* 244 */     bw.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 248 */     int _segment_size = Integer.parseInt(args[0]);
/* 249 */     String _transcripts_bed = args[1];
/* 250 */     String _path = args[2];
/*     */     try {
/* 252 */       new ProcessPutativeFusions(_segment_size, _transcripts_bed, _path);
/*     */     } catch (IOException ex) {
/* 254 */       System.out.println("Class requires as input:\n\tconstruct segment size\n\tannotation_bed\n\tand path (working directory) containing exons.bed, exons.fa files");
/*     */       
/* 256 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private void get_merged_transcripts_bed() throws IOException {
/* 261 */     BufferedReader br = new BufferedReader(new FileReader(this.transcripts_bed + ".merged"));
/* 262 */     String line = "";
/*     */     
/* 264 */     while ((line = br.readLine()) != null) {
/* 265 */       String id = line.split("\t")[0] + ":" + line.split("\t")[1] + "-" + line.split("\t")[2];
/*     */       
/* 267 */       this.transcripts_merged.put(id, line);
/*     */       
/* 269 */       for (String s : line.split("\t")[3].split(";")) {
/* 270 */         this.transcripts_locus.put(s.replace(".", ":"), id);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 275 */     br.close();
/*     */   }
/*     */   
/*     */   private boolean check_locus_overlap(String left_id, String right_id) {
/* 279 */     boolean overlap = false;
/*     */     
/* 281 */     String left = "";
/* 282 */     String right = "";
/*     */     
/* 284 */     if (this.transcripts_locus.containsKey(left_id)) {
/* 285 */       left = (String)this.transcripts_locus.get(left_id);
/*     */     }
/*     */     
/* 288 */     if (this.transcripts_locus.containsKey(right_id)) {
/* 289 */       right = (String)this.transcripts_locus.get(right_id);
/*     */     }
/*     */     
/*     */ 
/* 293 */     if (left.equalsIgnoreCase(right)) {
/* 294 */       overlap = true;
/*     */     }
/*     */     
/* 297 */     return overlap;
/*     */   }
/*     */ }
