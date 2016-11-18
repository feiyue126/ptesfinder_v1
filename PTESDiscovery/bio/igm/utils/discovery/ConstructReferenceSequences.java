/*     */ package bio.igm.utils.discovery;
/*     */ 
/*     */ import bio.igm.entities.ExonConstructs;
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
/*     */ 
/*     */ 
/*     */ public class ConstructReferenceSequences
/*     */ {
/*  35 */   String path = "";
/*     */   String mrnaPath;
/*  37 */   Map<String, String> putative_models = new HashMap();
/*  38 */   Map<String, String> transcripts = new HashMap();
/*  39 */   Map<String, ExonConstructs> constructs = new HashMap();
/*  40 */   Map<String, ExonConstructs> can_constructs = new HashMap();
/*  41 */   List<String> canonical = new ArrayList();
/*  42 */   List<String> ptes = new ArrayList();
/*  43 */   int segment_size = 0;
/*     */   private static Logger LOG;
/*     */   
/*     */   public ConstructReferenceSequences(String _path, String mrnafa, int segmentSize) throws IOException {
/*  47 */     this.path = _path;
/*  48 */     File f = new File(_path);
/*     */     try
/*     */     {
/*  51 */       if (f.isDirectory()) {
/*  52 */         LOG = new Logging(_path, ConstructReferenceSequences.class.getName()).setup();
/*     */       } else {
/*  54 */         LOG = new Logging(f.getParent(), ConstructReferenceSequences.class.getName()).setup();
/*     */       }
/*     */     } catch (IOException ex) {
/*  57 */       Logger.getLogger(ConstructReferenceSequences.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */     
/*  60 */     this.mrnaPath = mrnafa;
/*  61 */     this.segment_size = segmentSize;
/*     */     
/*  63 */     read_transcriptome_fasta();
/*  64 */     LOG.info("Finished reading Refseq Exons .. ");
/*     */     
/*  66 */     read_structures();
/*  67 */     this.constructs = generate_constructs(this.ptes);
/*  68 */     LOG.info("Finished generating PTES sequence constructs ");
/*     */     
/*  70 */     this.can_constructs = generate_constructs(this.canonical);
/*  71 */     LOG.info("Finished generating Canonical sequence constructs");
/*     */     
/*  73 */     writeConstructsToFile(this.constructs, "ptes");
/*  74 */     writeConstructsToFile(this.can_constructs, "canonical");
/*     */   }
/*     */   
/*     */   public void read_transcriptome_fasta() throws IOException
/*     */   {
/*  79 */     BufferedReader br = new BufferedReader(new FileReader(this.mrnaPath));
/*  80 */     String line = "";
/*  81 */     StringBuilder builder = new StringBuilder();
/*  82 */     while ((line = br.readLine()) != null) {
/*  83 */       builder.append(line);
/*  84 */       builder.append(System.getProperty("line.separator"));
/*     */     }
/*     */     
/*  87 */     br.close();
/*  88 */     String[] seqs = builder.toString().split(">");
/*  89 */     String seq = "";
/*     */     
/*  91 */     for (String s : seqs) {
/*  92 */       if (StringUtils.isNotBlank(s)) {
/*  93 */         String[] oneSeq = s.trim().split("\n");
/*  94 */         String id = oneSeq[0].trim().split(" ").length < 2 ? oneSeq[0] : processId(oneSeq[0]);
/*     */         
/*  96 */         seq = StringUtils.join(oneSeq, "", 1, oneSeq.length);
/*  97 */         this.transcripts.put(id, seq.toUpperCase());
/*     */       }
/*     */     }
/* 100 */     LOG.info("Finished loading transcripts\nSize: " + this.transcripts.size());
/*     */   }
/*     */   
/*     */   private String processId(String string)
/*     */   {
/* 105 */     String temp = "";
/* 106 */     String[] contents = string.split(" ")[0].split("_");
/* 107 */     if (contents.length > 4) {
/* 108 */       temp = contents[2] + "_" + contents[3] + "_" + contents[4];
/*     */     }
/*     */     
/* 111 */     return temp;
/*     */   }
/*     */   
/*     */   private Map<String, ExonConstructs> generate_constructs(List<String> structures) {
/* 115 */     Map<String, ExonConstructs> _constructs = new HashMap();
/* 116 */     for (String s : structures) {
/* 117 */       String refseq = s.split("\t")[0].split("\\.")[0];
/* 118 */       int exon5start = Integer.parseInt(s.split("\t")[1]);
/* 119 */       int exon5stop = Integer.parseInt(s.split("\t")[2]);
/* 120 */       int exon3start = Integer.parseInt(s.split("\t")[3]);
/* 121 */       int exon3stop = Integer.parseInt(s.split("\t")[4]);
/*     */       
/* 123 */       if (this.transcripts.containsKey(refseq)) {
/* 124 */         String seq = (String)this.transcripts.get(refseq);
/* 125 */         String seq1 = seq.substring(exon5start - 1, exon5stop);
/* 126 */         String seq2 = seq.substring(exon3start - 1, exon3stop);
/*     */         
/* 128 */         ExonConstructs construct = new ExonConstructs(s.split("\t")[0], seq1, seq2, this.segment_size);
/* 129 */         _constructs.put(construct.getId(), construct);
/*     */       }
/*     */     }
/*     */     
/* 133 */     return _constructs;
/*     */   }
/*     */   
/*     */   private void read_structures() throws FileNotFoundException, IOException
/*     */   {
/* 138 */     BufferedReader br = new BufferedReader(new FileReader(this.path + "TestTargets.txt"));
/* 139 */     String line = "";
/*     */     
/* 141 */     while ((line = br.readLine()) != null) {
/*     */       try {
/* 143 */         if (!this.ptes.contains(line.split("\t")[2])) {
/* 144 */           this.ptes.add(line.split("\t")[2] + "\t" + line.split("\t")[3] + "\t" + line.split("\t")[4] + "\t" + line.split("\t")[5] + "\t" + line.split("\t")[6]);
/*     */         }
/*     */         
/* 147 */         for (i = 7; i < line.split("\t").length;) {
/* 148 */           if (!this.canonical.contains(line.split("\t")[i])) {
/* 149 */             this.canonical.add(line.split("\t")[i] + "\t" + line.split("\t")[(i + 1)] + "\t" + line.split("\t")[(i + 2)] + "\t" + line.split("\t")[(i + 3)] + "\t" + line.split("\t")[(i + 4)]);
/*     */           }
/*     */           
/* 152 */           i += 5;
/*     */         }
/*     */       } catch (Exception e) { int i;
/* 155 */         System.out.println("Error! '" + line + "' does not conform..");
/*     */       }
/*     */     }
/* 158 */     LOG.info("Finished reading putative PTES models ");
/*     */   }
/*     */   
/*     */   private void writeConstructsToFile(Map<String, ExonConstructs> _constructs, String structure) throws IOException {
/* 162 */     String filename = "";
/* 163 */     if (structure.equalsIgnoreCase("ptes")) {
/* 164 */       filename = "PTESJoints.fasta";
/*     */     } else {
/* 166 */       filename = "CannonicalJoints.fasta";
/*     */     }
/*     */     
/* 169 */     BufferedWriter bw = new BufferedWriter(new FileWriter(this.path + filename));
/* 170 */     for (String s : _constructs.keySet()) {
/* 171 */       bw.write(">" + s + "\n" + ((ExonConstructs)_constructs.get(s)).getSequence() + "\n");
/*     */     }
/* 173 */     bw.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 178 */     String path = args[0];
/* 179 */     String epath = args[1];
/* 180 */     int segment_len = Integer.parseInt(args[2]);
/*     */     try {
/* 182 */       new ConstructReferenceSequences(path, epath, segment_len);
/*     */     } catch (IOException ex) {
/* 184 */       LOG.log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ }
