/*     */ package bio.igm.utils.discovery;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
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
/*     */ public class DetectExonShuffling
/*     */ {
/*     */   String path;
/*     */   String infile;
/*  25 */   boolean shuffled = true;
/*     */   
/*     */   public DetectExonShuffling(String _path, String _infile, boolean _shuffled) throws IOException {
/*  28 */     this.path = _path;
/*  29 */     this.infile = _infile;
/*  30 */     this.shuffled = _shuffled;
/*     */     
/*  32 */     process_merged_sam();
/*     */   }
/*     */   
/*     */   private void process_merged_sam() throws IOException {
/*  36 */     String outfile = "";
/*     */     
/*  38 */     if (this.shuffled)
/*     */     {
/*  40 */       outfile = this.path + "/processedSAM.txt";
/*     */     } else {
/*  42 */       outfile = this.path + "/processed_canonical_anchors.txt";
/*     */     }
/*  44 */     BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
/*  45 */     BufferedReader br = new BufferedReader(new FileReader(this.infile));
/*     */     String line;
/*  47 */     while ((line = br.readLine()) != null) {
/*  48 */       int left_start = Integer.parseInt(line.split("\t")[3]);
/*  49 */       int right_start = Integer.parseInt(line.split("\t")[8]);
/*  50 */       int left_stop = left_start + determine_shift(line.split("\t")[4]);
/*  51 */       int right_stop = right_start + determine_shift(line.split("\t")[9]);
/*     */       
/*  53 */       int orientation = Integer.parseInt(line.split("\t")[1]) > 0 ? 1 : -1;
/*     */       
/*  55 */       if (this.shuffled) {
/*  56 */         switch (orientation) {
/*     */         case -1: 
/*  58 */           if (right_start < left_stop) {
/*  59 */             bw.write(line.split("\t")[0] + "\t" + line.split("\t")[2] + "\t" + left_start + "\t" + right_start + "\t" + orientation + "\t" + (left_stop - left_start) + "\t" + (right_stop - right_start) + "\n");
/*     */           }
/*     */           
/*     */           break;
/*     */         case 1: 
/*  64 */           if (left_start < right_stop) {
/*  65 */             bw.write(line.split("\t")[0] + "\t" + line.split("\t")[2] + "\t" + left_start + "\t" + right_start + "\t" + orientation + "\t" + (left_stop - left_start) + "\t" + (right_stop - right_start) + "\n");
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */       } else {
/*  71 */         switch (orientation) {
/*     */         case -1: 
/*  73 */           if (right_start > left_stop) {
/*  74 */             bw.write(line.split("\t")[0] + "\t" + line.split("\t")[2] + "\t" + left_start + "\t" + right_start + "\t" + orientation + "\t" + (left_stop - left_start) + "\t" + (right_stop - right_start) + "\n");
/*     */           }
/*     */           
/*     */           break;
/*     */         case 1: 
/*  79 */           if (left_start > right_stop) {
/*  80 */             bw.write(line.split("\t")[0] + "\t" + line.split("\t")[2] + "\t" + left_start + "\t" + right_start + "\t" + orientation + "\t" + (left_stop - left_start) + "\t" + (right_stop - right_start) + "\n");
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*  87 */     br.close();
/*  88 */     bw.close();
/*     */   }
/*     */   
/*     */   private int determine_shift(String cigar)
/*     */   {
/*  93 */     int shift = 0;
/*  94 */     String[] cigar_numbers = cigar.split("\\D");
/*  95 */     for (String s : cigar_numbers) {
/*     */       try {
/*  97 */         shift += Integer.parseInt(s);
/*     */       } catch (Exception e) {
/*  99 */         shift += 0;
/*     */       }
/*     */     }
/* 102 */     return shift;
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try {
/* 108 */       new DetectExonShuffling(args[0], args[1], Boolean.parseBoolean(args[2]));
/*     */     } catch (IOException ex) {
/* 110 */       Logger.getLogger(DetectExonShuffling.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
