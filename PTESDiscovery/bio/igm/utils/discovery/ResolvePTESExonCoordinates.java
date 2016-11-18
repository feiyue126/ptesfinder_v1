/*     */ package bio.igm.utils.discovery;
/*     */ 
/*     */ import bio.igm.entities.Exons;
/*     */ import bio.igm.entities.MappedAnchors;
/*     */ import bio.igm.entities.Transcript;
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
/*     */ public class ResolvePTESExonCoordinates
/*     */ {
/*  29 */   Map<String, Transcript> refseq = new HashMap();
/*  30 */   Map<String, String> to_print = new HashMap();
/*     */   String eid;
/*     */   String path;
/*     */   private static Logger LOG;
/*     */   
/*     */   public ResolvePTESExonCoordinates(String _path, String _eid) throws IOException {
/*  36 */     this.path = _path;
/*  37 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  40 */       if (f.isDirectory()) {
/*  41 */         LOG = new Logging(_path, ResolvePTESExonCoordinates.class.getName()).setup();
/*     */       } else {
/*  43 */         LOG = new Logging(f.getParent(), ResolvePTESExonCoordinates.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  46 */       Logger.getLogger(ResolvePTESExonCoordinates.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*  48 */     this.eid = _eid;
/*     */     
/*  50 */     getRefSeqCoord();
/*     */     
/*  52 */     read_processed_sam();
/*     */     
/*  54 */     LOG.info("Finished resolving shuffled coordinates to exons..");
/*  55 */     writeToFile();
/*     */   }
/*     */   
/*     */   private void getRefSeqCoord() throws IOException
/*     */   {
/*  60 */     BufferedReader br = new BufferedReader(new FileReader(this.eid));
/*  61 */     String line = "";
/*     */     
/*  63 */     while ((line = br.readLine()) != null) {
/*  64 */       String[] contents = line.trim().split("\t");
/*     */       
/*  66 */       Transcript transcript = new Transcript(contents[0].toUpperCase());
/*     */       
/*     */ 
/*  69 */       if (contents.length > 3) {
/*  70 */         String[] start = StringUtils.split(contents[2]);
/*  71 */         String[] end = StringUtils.split(contents[3]);
/*     */         
/*  73 */         for (int i = 0; i < start.length; i++) {
/*  74 */           Exons exon = new Exons(transcript, Integer.parseInt(start[i]), Integer.parseInt(end[i]));
/*  75 */           exon.setOrder(i + 1);
/*     */           
/*  77 */           transcript.addExon(exon);
/*     */         }
/*     */         
/*  80 */         this.refseq.put(transcript.getRefseq(), transcript);
/*     */       }
/*  82 */       contents = null;
/*     */     }
/*  84 */     LOG.info("Transcripts: " + this.refseq.size());
/*     */     
/*  86 */     br.close();
/*     */   }
/*     */   
/*     */   private void read_processed_sam() throws IOException
/*     */   {
/*  91 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "processedSAM.txt"));
/*  92 */     String line = "";
/*     */     
/*  94 */     while ((line = br.readLine()) != null) {
/*     */       try
/*     */       {
/*  97 */         String locus = line.split("\t")[1].contains("ref") ? processID(line.split("\t")[1]) : line.split("\t")[1];
/*     */         
/*  99 */         int left = Integer.parseInt(line.split("\t")[2]);
/* 100 */         int right = Integer.parseInt(line.split("\t")[3]);
/* 101 */         int o = Integer.parseInt(line.split("\t")[4]);
/*     */         
/* 103 */         checkAnchorEquality(left, right, locus);
/*     */       } catch (Exception e) {
/* 105 */         LOG.info("Error processing this read: " + line);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 110 */     br.close();
/*     */   }
/*     */   
/*     */   private static String processID(String string)
/*     */   {
/* 115 */     String id = "";
/* 116 */     String[] target = string.split("ref");
/* 117 */     id = target[1].split("\\.")[0].replaceAll("\\..*|[^a-zA-Z0-9_]", "");
/*     */     
/* 119 */     return id;
/*     */   }
/*     */   
/*     */   private void checkAnchorEquality(int left, int right, String locus)
/*     */   {
/* 124 */     Transcript transcript = null;
/* 125 */     MappedAnchors anchors = null;
/*     */     
/* 127 */     if (this.refseq.containsKey(locus))
/*     */     {
/* 129 */       transcript = (Transcript)this.refseq.get(locus);
/* 130 */       int x = Math.max(left, right);
/* 131 */       int y = Math.min(left, right);
/* 132 */       anchors = new MappedAnchors(x, y, transcript);
/*     */       
/* 134 */       if (anchors.checkJunction())
/*     */       {
/* 136 */         this.to_print.put(anchors.getId(), anchors.getTo_print());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void writeToFile()
/*     */     throws IOException
/*     */   {
/* 146 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + "TestTargets.txt"));
/* 147 */     for (String s : this.to_print.values()) {
/* 148 */       bw.write(s + "\n");
/*     */     }
/* 150 */     bw.close();
/*     */   }
/*     */   
/*     */   public Map<String, Transcript> getRefseq() {
/* 154 */     return this.refseq;
/*     */   }
/*     */   
/*     */   public void setRefseq(Map<String, Transcript> refseq) {
/* 158 */     this.refseq = refseq;
/*     */   }
/*     */   
/*     */   public String getEid() {
/* 162 */     return this.eid;
/*     */   }
/*     */   
/*     */   public void setEid(String eid) {
/* 166 */     this.eid = eid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 175 */       String path = args[0];
/* 176 */       String eid = args[1];
/*     */       
/* 178 */       r = new ResolvePTESExonCoordinates(path, eid);
/*     */     } catch (IOException ex) { ResolvePTESExonCoordinates r;
/* 180 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
