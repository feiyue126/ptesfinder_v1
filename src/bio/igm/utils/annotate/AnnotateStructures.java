/*     */ package bio.igm.utils.annotate;
/*     */ 
/*     */ import bio.igm.utils.init.Logging;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class AnnotateStructures
/*     */ {
/*     */   String path;
/*     */   String counts_path;
/*  31 */   Map<String, Integer> structures = new HashMap();
/*  32 */   Map<String, String> annotated_structures = new HashMap();
/*  33 */   Map<String, String> unmerged_structures = new HashMap();
/*  34 */   Map<String, Map<Integer, String>> genes = new HashMap();
/*     */   public static Logger LOG;
/*     */   
/*     */   public AnnotateStructures(String _path, String _counts_path) throws IOException {
/*  38 */     this.path = _path;
/*  39 */     this.counts_path = _counts_path;
/*  40 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  43 */       if (f.isDirectory()) {
/*  44 */         LOG = new Logging(_path, AnnotateStructures.class.getName()).setup();
/*     */       } else {
/*  46 */         LOG = new Logging(f.getParent(), AnnotateStructures.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  49 */       Logger.getLogger(AnnotateStructures.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*  52 */     read_structures();
/*  53 */     read_transcript_annotation();
/*     */     
/*  55 */     annotate_structures();
/*     */     
/*  57 */     writeToFile();
/*     */   }
/*     */   
/*     */   private void read_structures() throws IOException {
/*  61 */     BufferedReader br = new BufferedReader(new FileReader(this.counts_path));
/*  62 */     String line = "";
/*     */     
/*  64 */     while ((line = br.readLine()) != null) {
/*  65 */       if (line.split("\t")[0].length() > 30)
/*     */       {
/*  67 */         String strand = line.contains("+") ? "+" : "-";
/*  68 */         String coord = line.split("\t")[0].split("_")[0];
/*  69 */         String record = coord.split(":")[0] + "\t" + coord.split(":")[1].split("-")[0] + "\t" + coord.split(":")[1].split("-")[1] + "\t" + line + "\t" + strand;
/*     */         
/*  71 */         this.annotated_structures.put(coord, record);
/*     */       }
/*  73 */       else if (this.structures.containsKey(line.split("\t")[0])) {
/*  74 */         int count = ((Integer)this.structures.get(line.split("\t")[0])).intValue() + Integer.parseInt(line.split("\t")[1]);
/*  75 */         this.structures.put(line.split("\t")[0], Integer.valueOf(count));
/*     */       } else {
/*  77 */         this.structures.put(line.split("\t")[0], Integer.valueOf(Integer.parseInt(line.split("\t")[1])));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void read_transcript_annotation()
/*     */     throws FileNotFoundException, IOException
/*     */   {
/*  85 */     BufferedReader br = new BufferedReader(new FileReader(this.path));
/*  86 */     String line = "";
/*     */     
/*  88 */     while ((line = br.readLine()) != null) {
/*  89 */       String refseq = line.split("\t")[3];
/*     */       
/*  91 */       if (!refseq.startsWith("N")) {
/*  92 */         String _refseq = refseq.split("_")[0].replace(".", ":");
/*  93 */         String _line = "";
/*  94 */         String[] content = line.split("\t");
/*  95 */         content[3] = _refseq.toUpperCase();
/*  96 */         for (int i = 0; i < content.length; i++) {
/*  97 */           _line = _line + content[i] + "\t";
/*     */         }
/*  99 */         refseq = _refseq.toUpperCase();
/* 100 */         line = _line;
/*     */       }
/* 102 */       int index = Integer.parseInt(line.split("\t")[4]);
/*     */       
/* 104 */       if (this.genes.containsKey(refseq)) {
/* 105 */         ((Map)this.genes.get(refseq)).put(Integer.valueOf(index), line);
/*     */       } else {
/* 107 */         Map<Integer, String> temp = new HashMap();
/* 108 */         temp.put(Integer.valueOf(index), line);
/* 109 */         this.genes.put(refseq, temp);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void annotate_structures() {
/* 115 */     Map<String, String> temp_ids = new HashMap();
/* 116 */     Map<String, String> temp_counts = new HashMap();
/* 117 */     for (String s : this.structures.keySet()) {
/*     */       try {
/* 119 */         String refseq = s.split("\\.")[0];
/* 120 */         int count = ((Integer)this.structures.get(s)).intValue();
/* 121 */         int prime5 = Integer.parseInt(s.split("\\.")[1]) - 1;
/* 122 */         int prime3 = Integer.parseInt(s.split("\\.")[2]) - 1;
/*     */         
/* 124 */         if (this.genes.containsKey(refseq)) {
/* 125 */           Map<Integer, String> temp = (Map)this.genes.get(refseq);
/* 126 */           List<Integer> order = new ArrayList(temp.keySet());
/* 127 */           Collections.sort(order);
/* 128 */           Integer[] index = new Integer[temp.size()];
/* 129 */           index = (Integer[])order.toArray(index);
/* 130 */           String exon5 = (String)temp.get(index[prime5]);
/* 131 */           String exon3 = (String)temp.get(index[prime3]);
/* 132 */           int start = 0;
/* 133 */           int end = 0;
/*     */           
/*     */ 
/* 136 */           boolean negative_strand = false;
/*     */           
/* 138 */           String[] contents = exon5.split("\t");
/*     */           
/* 140 */           String id = contents[0] + ':';
/*     */           
/* 142 */           if (contents[5].equalsIgnoreCase("-")) {
/* 143 */             negative_strand = true;
/* 144 */             start = Integer.parseInt(contents[1]);
/*     */           } else {
/* 146 */             start = Integer.parseInt(contents[2]);
/*     */           }
/*     */           
/* 149 */           String bedformat = start + "";
/* 150 */           String appended = "";
/* 151 */           id = id + start + "-";
/*     */           
/*     */ 
/* 154 */           contents = exon3.split("\t");
/*     */           
/* 156 */           if (negative_strand) {
/* 157 */             end = Integer.parseInt(contents[2]);
/*     */           } else {
/* 159 */             end = Integer.parseInt(contents[1]);
/*     */           }
/* 161 */           if (start < end) {
/* 162 */             bedformat = contents[0] + "\t" + bedformat + "\t" + end + "\t";
/*     */           } else {
/* 164 */             bedformat = contents[0] + "\t" + end + "\t" + bedformat + "\t";
/*     */           }
/* 166 */           id = id + end;
/*     */           
/*     */ 
/* 169 */           if (this.annotated_structures.containsKey(id)) {
/* 170 */             int countsum = Integer.parseInt(((String)this.annotated_structures.get(id)).split("\t")[4]) + count;
/* 171 */             bedformat = bedformat + s + "\t" + countsum + "\t" + contents[5];
/* 172 */             appended = appended + s + "\t" + id + "\t" + countsum;
/* 173 */             this.annotated_structures.put(id, bedformat);
/*     */           }
/*     */           else {
/* 176 */             bedformat = bedformat + s + "\t" + count + "\t" + contents[5];
/* 177 */             appended = appended + s + "\t" + id + "\t" + count;
/* 178 */             this.annotated_structures.put(id, bedformat);
/*     */           }
/* 180 */           temp_ids.put(s, id);
/* 181 */           temp_counts.put(id, appended);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 185 */         System.err.println("Error annotating " + s);
/*     */       }
/*     */     }
/*     */     
/* 189 */     for (String s : temp_ids.keySet()) {
/* 190 */       String id = (String)temp_ids.get(s);
/* 191 */       if (temp_counts.containsKey(id.trim())) {
/* 192 */         String temp = s + "\t" + id + "\t" + ((String)temp_counts.get(id.trim())).split("\t")[2];
/* 193 */         this.unmerged_structures.put(s, temp);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException
/*     */   {
/* 200 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.counts_path + ".bed"));
/* 201 */     BufferedWriter bw1 = new BufferedWriter(new FileWriter(this.counts_path + ".unmerged"));
/* 202 */     for (String s : this.annotated_structures.values()) {
/* 203 */       bw.write(s + "\n");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 208 */     for (String s : this.unmerged_structures.values()) {
/* 209 */       bw1.write(s + "\n");
/*     */     }
/* 211 */     bw1.close();
/* 212 */     bw.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 216 */     String exons_bed = args[0];
/* 217 */     String junction_counts = args[1];
/*     */     try {
/* 219 */       new AnnotateStructures(exons_bed, junction_counts);
/*     */     } catch (IOException ex) {
/* 221 */       Logger.getLogger(AnnotateStructures.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
