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
/*     */ public class ConvertBedToCoordinates
/*     */ {
/*     */   String path;
/*  26 */   Map<String, String> genes = new HashMap();
/*  27 */   Map<String, String> coords = new HashMap();
/*     */   private static Logger LOG;
/*     */   
/*     */   public ConvertBedToCoordinates(String _path, String _wd) throws IOException {
/*  31 */     this.path = _path;
/*  32 */     File f = new File(_wd);
/*     */     try
/*     */     {
/*  35 */       if (f.isDirectory()) {
/*  36 */         LOG = new Logging(_wd, ConvertBedToCoordinates.class.getName()).setup();
/*     */       } else {
/*  38 */         LOG = new Logging(f.getParent(), ConvertBedToCoordinates.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  41 */       Logger.getLogger(ConvertBedToCoordinates.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  46 */     read_input_bed();
/*  47 */     convert_to_coords();
/*     */     
/*  49 */     writeToFile();
/*     */   }
/*     */   
/*     */   private void read_input_bed() throws IOException
/*     */   {
/*  54 */     LOG.entering(getClass().getName(), "read_input_bed() - Preparing Coordinates File...");
/*  55 */     BufferedReader br = new BufferedReader(new FileReader(this.path));
/*  56 */     String line = "";
/*     */     
/*  58 */     while ((line = br.readLine()) != null) {
/*  59 */       String id = line.split("\t")[3];
/*     */       
/*  61 */       if ((id.startsWith("NM")) || (id.startsWith("NR"))) {
/*  62 */         this.genes.put(id, line);
/*     */       } else {
/*  64 */         String _id = id.split("_")[0].replace(".", ":");
/*  65 */         String _line = "";
/*  66 */         String[] content = line.split("\t");
/*  67 */         content[3] = _id;
/*  68 */         for (int i = 0; i < content.length; i++) {
/*  69 */           _line = _line + content[i] + "\t";
/*     */         }
/*     */         
/*     */ 
/*  73 */         this.genes.put(_id, _line.trim());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void convert_to_coords()
/*     */   {
/*  81 */     LOG.entering(getClass().getName(), "convert_to_coords() - Converting input BED to coords file");
/*  82 */     for (String s : this.genes.keySet()) {
/*  83 */       String new_coord = "";
/*  84 */       String[] sizes = null;
/*  85 */       if (((String)this.genes.get(s)).split("\t")[5].equalsIgnoreCase("-")) {
/*  86 */         sizes = negative_strand_reverse(((String)this.genes.get(s)).split("\t")[10].split(","));
/*     */       } else {
/*  88 */         sizes = ((String)this.genes.get(s)).split("\t")[10].split(",");
/*     */       }
/*     */       
/*  91 */       String starts = "";
/*  92 */       String ends = "";
/*  93 */       int x = 0;
/*  94 */       int y = 0;
/*  95 */       new_coord = s + "\t" + s;
/*  96 */       for (int i = 0; i < sizes.length; i++) {
/*  97 */         starts = starts + (x + 1) + " ";
/*  98 */         y = x + Integer.parseInt(sizes[i]);
/*  99 */         ends = ends + y + " ";
/* 100 */         x = y;
/*     */       }
/* 102 */       new_coord = new_coord + "\t" + starts + "\t" + ends;
/* 103 */       this.coords.put(s, new_coord);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String[] negative_strand_reverse(String[] data)
/*     */   {
/* 110 */     int left = 0;
/* 111 */     int right = data.length - 1;
/*     */     
/* 113 */     while (left < right)
/*     */     {
/* 115 */       String temp = data[left];
/* 116 */       data[left] = data[right];
/* 117 */       data[right] = temp;
/*     */       
/*     */ 
/* 120 */       left++;
/* 121 */       right--;
/*     */     }
/* 123 */     return data;
/*     */   }
/*     */   
/*     */   private void writeToFile() throws IOException {
/* 127 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + ".coords"));
/*     */     
/* 129 */     for (String s : this.coords.values()) {
/* 130 */       bw.write(s + "\n");
/*     */     }
/* 132 */     bw.close();
/* 133 */     LOG.info("Finished processing coordinates File...");
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 137 */     String path = args[0];
/* 138 */     String wd = args[1];
/*     */     try {
/* 140 */       new ConvertBedToCoordinates(path, wd);
/*     */     } catch (IOException ex) {
/* 142 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
