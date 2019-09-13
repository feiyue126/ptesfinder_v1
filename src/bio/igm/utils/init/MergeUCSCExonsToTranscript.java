/*     */ package bio.igm.utils.init;
/*     */ 
/*     */ import bio.igm.entities.Exons;
/*     */ import bio.igm.entities.Transcript;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class MergeUCSCExonsToTranscript
/*     */ {
/*     */   String path;
/*  33 */   Map<String, Exons> exons = new HashMap();
/*  34 */   Map<String, Transcript> transcripts = new HashMap();
/*     */   private static Logger LOG;
/*     */   
/*     */   public MergeUCSCExonsToTranscript(String _path, String _wd) throws IOException {
/*  38 */     this.path = _path;
/*  39 */     File f = new File(_wd);
/*     */     try
/*     */     {
/*  42 */       if (f.isDirectory()) {
/*  43 */         LOG = new Logging(_wd, MergeUCSCExonsToTranscript.class.getName()).setup();
/*     */       } else {
/*  45 */         LOG = new Logging(f.getParent(), MergeUCSCExonsToTranscript.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  48 */       Logger.getLogger(MergeUCSCExonsToTranscript.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*  50 */     readExons();
/*  51 */     buildTranscripts();
/*  52 */     writeToFile();
/*     */   }
/*     */   
/*     */   private void readExons() throws IOException {
/*  56 */     BufferedReader br = new BufferedReader(new FileReader(this.path));
/*  57 */     StringBuilder builder = new StringBuilder();
/*     */     
/*  59 */     String line = "";
/*     */     
/*  61 */     while ((line = br.readLine()) != null) {
/*  62 */       builder.append(line);
/*  63 */       builder.append(System.getProperty("line.separator"));
/*     */     }
/*  65 */     String[] seqs = builder.toString().split(">");
/*     */     
/*  67 */     for (String s : seqs) {
/*  68 */       if (StringUtils.isNotBlank(s)) {
/*  69 */         String[] oneSeq = s.trim().split("\n");
/*  70 */         String id = oneSeq[0];
/*     */         
/*  72 */         String seq = StringUtils.join(oneSeq, "", 1, oneSeq.length);
/*  73 */         Exons e = new Exons(id, seq);
/*  74 */         this.exons.put(id, e);
/*     */       }
/*     */     }
/*  77 */     LOG.info("Number of Exons: " + this.exons.size());
/*     */   }
/*     */   
/*     */   private void buildTranscripts() {
/*  81 */     for (Exons e : this.exons.values()) {
/*  82 */       if (this.transcripts.containsKey(e.getRefid())) {
/*  83 */         ((Transcript)this.transcripts.get(e.getRefid())).addExon(e);
/*     */       } else {
/*  85 */         Transcript t = new Transcript(e.getRefid());
/*  86 */         t.addExon(e);
/*  87 */         this.transcripts.put(t.getRefseq(), t);
/*     */       }
/*     */     }
/*     */     
/*  91 */     LOG.info("Finished Adding Exons to Transcripts.. ");
/*     */     
/*  93 */     for (Transcript t : this.transcripts.values())
/*     */     {
/*  95 */       List<Integer> temp = new ArrayList(t.getExons().keySet());
/*  96 */       Collections.sort(temp);
/*  97 */       Integer[] index = new Integer[temp.size()];
/*  98 */       index = (Integer[])temp.toArray(index);
/*  99 */       String temp_seq = "";
/* 100 */       for (int i = 0; i < index.length; i++) {
/* 101 */         temp_seq = temp_seq + ((Exons)t.getExons().get(index[i])).getSequence();
/*     */       }
/* 103 */       t.setSequence(temp_seq);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException
/*     */   {
/* 109 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + ".mrna"));
/* 110 */     int counter = 0;
/* 111 */     for (Transcript t : this.transcripts.values()) {
/* 112 */       bw.write(">" + t.getRefseq() + "\n" + t.getSequence() + "\n");
/* 113 */       counter++;
/*     */     }
/* 115 */     bw.close();
/* 116 */     LOG.info("Number of mRNA Sequences: " + counter);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 120 */     String path = args[0];
/* 121 */     String wd = args[1];
/*     */     try {
/* 123 */       new MergeUCSCExonsToTranscript(path, wd);
/*     */     } catch (IOException ex) {
/* 125 */       Logger.getLogger(MergeUCSCExonsToTranscript.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
