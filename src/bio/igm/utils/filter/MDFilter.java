/*     */ package bio.igm.utils.filter;
/*     */ 
/*     */ import bio.igm.entities.PTES;
/*     */ import bio.igm.entities.Reads;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class MDFilter
/*     */ {
/*  29 */   Map<String, PTES> ptes = new HashMap();
/*  30 */   Map<String, List<Reads>> canonical = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MDFilter(Map<String, PTES> putative, Map<String, Reads> reads, int span, double pid)
/*     */   {
/*  40 */     Reads read = null;
/*  41 */     PTES p = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  47 */     for (String s : reads.keySet()) {
/*  48 */       read = (Reads)reads.get(s);
/*  49 */       int junction = read.getRefJunction() - read.getStart();
/*  50 */       if ((read.getEditDistance().split(":")[2].equalsIgnoreCase("0")) && (putative.containsKey(read.getTarget()))) {
/*  51 */         p = (PTES)putative.get(read.getTarget());
/*  52 */         p.addRead(read);
/*  53 */         p.setCount(p.getReads().size());
/*  54 */         p.setConfirmed(true);
/*     */         
/*  56 */         putative.put(p.getId(), p);
/*     */       } else {
/*  58 */         String[] parsedMd = parseMD(read.getMdfield(), read.getCigar(), junction);
/*  59 */         if ((checkJunctionSpan(read, parsedMd, span, pid)) && (putative.containsKey(read.getTarget())))
/*     */         {
/*  61 */           p = (PTES)putative.get(read.getTarget());
/*  62 */           p.addRead(read);
/*  63 */           p.setCount(p.getReads().size());
/*  64 */           p.setSpanned(true);
/*     */           
/*  66 */           putative.put(p.getId(), p);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  71 */     this.ptes = putative;
/*     */   }
/*     */   
/*     */   public MDFilter(Map<String, Reads> reads, int span, double pid, String path) throws IOException
/*     */   {
/*  76 */     Reads read = null;
/*  77 */     List<Reads> temp = null;
/*  78 */     Map<String, Integer> counts = new HashMap();
/*     */     
/*  80 */     BufferedWriter bpR = new BufferedWriter(new FileWriter(path + "PTESReads"));
/*  81 */     BufferedWriter bw = new BufferedWriter(new FileWriter(path + "ptescounts.tsv"));
/*  82 */     BufferedWriter bwJ = new BufferedWriter(new FileWriter(path + "junctions.fa"));
/*  83 */     BufferedWriter bwP = new BufferedWriter(new FileWriter(path + "pid.tsv"));
/*     */     
/*  85 */     BufferedWriter bf = new BufferedWriter(new FileWriter(path + "junctional-filtered.sam"));
/*  86 */     bwP.write("Read_ID\tPTES_ID\tEdit_Distance\tLeftPID\tRightPID\n");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  91 */     for (String s : reads.keySet()) {
/*  92 */       read = (Reads)reads.get(s);
/*  93 */       int junction = read.getRefJunction() - read.getStart();
/*  94 */       String[] parsedMd = parseMD(read.getMdfield(), read.getCigar(), junction);
/*  95 */       if ((checkJunctionSpan(read, parsedMd, span, pid)) || (read.getEditDistance().equalsIgnoreCase("NM:i:0")))
/*     */       {
/*  97 */         bpR.write(read.getLine() + "\n");
/*  98 */         bwP.write(read.getId() + "\t" + read.getTarget() + "\t" + read.getEditDistance() + "\t" + read.getLeftpid() + "\t" + read.getRightpid() + "\n");
/*  99 */         bwJ.write(">" + read.getId() + "\t" + read.getTarget() + "\tStart: " + read.getStart() + "\tJunction:" + (read.getRefJunction() - read.getStart() + read.getJunctionShift()) + "\t" + read.getJunctionSeq() + "\t" + read.getJunctionShift() + "\t" + read.getHex() + "\t" + read.getEditDistance() + "\t" + read.getMdfield() + "\t" + read.getCigar() + "\n" + read.getSequence() + "\n" + read.getMdTransformed() + "\n");
/*     */         
/* 101 */         if (counts.containsKey(read.getTarget())) {
/* 102 */           int x = ((Integer)counts.get(read.getTarget())).intValue() + 1;
/* 103 */           counts.put(read.getTarget(), Integer.valueOf(x));
/*     */         } else {
/* 105 */           counts.put(read.getTarget(), Integer.valueOf(1));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 110 */         bf.write(read.getLine() + "\n");
/*     */       }
/*     */     }
/*     */     
/* 114 */     for (String s : counts.keySet()) {
/* 115 */       bw.write(s + "\t" + counts.get(s) + "\n");
/*     */     }
/*     */     
/* 118 */     bpR.close();
/* 119 */     bwJ.close();
/* 120 */     bwP.close();
/* 121 */     bf.close();
/* 122 */     bw.close();
/*     */   }
/*     */   
/*     */   public MDFilter(String path, int span, double pid) throws IOException
/*     */   {
/* 127 */     BufferedReader br = new BufferedReader(new FileReader(path + "canonical.sam"));
/* 128 */     Map<String, Integer> counts = new HashMap();
/*     */     
/* 130 */     BufferedWriter bcR = new BufferedWriter(new FileWriter(path + "flanking-canonical-reads.sam"));
/* 131 */     BufferedWriter bw = new BufferedWriter(new FileWriter(path + "flanking-canonical-counts.tsv"));
/* 132 */     String line = "";
/*     */     
/* 134 */     while ((line = br.readLine()) != null) {
/* 135 */       Reads read = new Reads(line);
/* 136 */       int junction = read.getRefJunction() - read.getStart();
/* 137 */       String[] parsedMd = parseMD(read.getMdfield(), read.getCigar(), junction);
/* 138 */       if (checkJunctionSpan(read, parsedMd, span, pid)) {
/* 139 */         bcR.write(read.getLine() + "\n");
/* 140 */         if (counts.containsKey(read.getTarget())) {
/* 141 */           int x = ((Integer)counts.get(read.getTarget())).intValue() + 1;
/* 142 */           counts.put(read.getTarget(), Integer.valueOf(x));
/*     */         } else {
/* 144 */           counts.put(read.getTarget(), Integer.valueOf(1));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 149 */     for (String s : counts.keySet()) {
/* 150 */       bw.write(s + "\t" + counts.get(s) + "\n");
/*     */     }
/* 152 */     bw.close();
/* 153 */     bcR.close();
/* 154 */     br.close();
/*     */   }
/*     */   
/*     */   private String[] parseMD(String md, String cigar, int junction) {
/* 158 */     StringBuilder seq = new StringBuilder();
/* 159 */     String temp = "";
/*     */     
/* 161 */     String pattern = "[0-9]+";
/* 162 */     String[] contents = md.split(":");
/*     */     
/* 164 */     Pattern p = Pattern.compile(pattern);
/* 165 */     Matcher m = p.matcher(contents[2]);
/*     */     
/* 167 */     int counter = 0;
/* 168 */     List<Integer> starts = new ArrayList();
/* 169 */     List<Integer> ends = new ArrayList();
/* 170 */     List<Integer> readGroups = new ArrayList();
/*     */     
/* 172 */     while (m.find()) {
/* 173 */       starts.add(Integer.valueOf(m.start()));
/* 174 */       ends.add(Integer.valueOf(m.end()));
/*     */       
/* 176 */       readGroups.add(Integer.valueOf(Integer.parseInt(m.group())));
/*     */       
/* 178 */       counter++;
/*     */     }
/*     */     
/* 181 */     contents[2] = (contents[2] + " ");
/* 182 */     for (int i = 0; i < readGroups.size(); i++) {
/* 183 */       for (int j = 0; j < ((Integer)readGroups.get(i)).intValue(); j++) {
/* 184 */         seq.append("M");
/*     */       }
/* 186 */       seq.append(contents[2].charAt(((Integer)ends.get(i)).intValue()));
/*     */     }
/* 188 */     temp = seq.toString().replaceAll("[^a-zA-Z0-9]", "");
/* 189 */     temp.trim();
/*     */     
/* 191 */     return parseIndels(temp, cigar, junction);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] parseIndels(String seq, String cigar, int junction)
/*     */   {
/* 202 */     List result = new ArrayList();
/* 203 */     List<Integer> minShift = new ArrayList();
/* 204 */     int shift = 0;
/* 205 */     int unshift = 0;
/* 206 */     int counter2 = 0;
/* 207 */     Map<Integer, Character> index = new HashMap();
/*     */     
/* 209 */     List<Integer> ind = new ArrayList();
/* 210 */     StringBuilder sb = new StringBuilder(seq);
/* 211 */     String pattern = "[A-Za-z]";
/*     */     
/* 213 */     Pattern p = Pattern.compile(pattern);
/* 214 */     Matcher m = p.matcher(cigar);
/*     */     
/* 216 */     while (m.find()) {
/* 217 */       char c = m.group().charAt(0);
/* 218 */       index.put(Integer.valueOf(m.end()), Character.valueOf(c));
/*     */       
/* 220 */       ind.add(Integer.valueOf(m.end()));
/*     */     }
/*     */     
/* 223 */     int counter = 0;
/*     */     
/*     */ 
/* 226 */     for (Iterator i$ = ind.iterator(); i$.hasNext();) {
/* 227 */       int z = ((Integer)i$.next()).intValue();
/* 228 */       int pos = z;
/* 229 */       c = ((Character)index.get(Integer.valueOf(z))).charValue();
/* 230 */       switch (c) {
/*     */       case 'I': 
/* 232 */         result = processDetail(cigar, c, pos);
/* 233 */         break;
/*     */       case 'D': 
/* 235 */         result = processDetail(cigar, c, pos);
/* 236 */         break;
/*     */       case 'S': 
/* 238 */         result = processDetail(cigar, c, pos);
/* 239 */         c = 's';
/* 240 */         break;
/*     */       default: 
/* 242 */         result = processDetail(cigar, c, pos);
/*     */       }
/*     */       
/* 245 */       if (c != 'M')
/* 246 */         for (a = 1; a < result.size();) {
/* 247 */           for (int i = 0; i < ((Integer)result.get(a)).intValue(); i++) {
/* 248 */             sb.insert(((Integer)result.get(a - 1)).intValue() + i, c);
/* 249 */             if (c == 'D') {
/* 250 */               counter--;
/*     */             }
/* 252 */             counter++;
/* 253 */             counter2++;
/*     */           }
/*     */           
/* 256 */           if (((Integer)result.get(a - 1)).intValue() < junction) {
/* 257 */             shift = counter;
/* 258 */             unshift = counter2;
/*     */           }
/* 260 */           a += 2;
/*     */         }
/*     */     }
/*     */     char c;
/*     */     int a;
/* 265 */     String[] res = { sb.toString(), shift + "", unshift + "" };
/*     */     
/* 267 */     return res;
/*     */   }
/*     */   
/*     */   private List<Integer> processDetail(String cigar, char c, int pos) {
/* 271 */     List<Integer> result = new ArrayList();
/*     */     
/* 273 */     int start = 0;
/* 274 */     int width = 0;
/*     */     
/* 276 */     Pattern p = Pattern.compile("[0-9]+");
/* 277 */     Matcher m = p.matcher(cigar.substring(0, pos));
/* 278 */     start = 0;
/*     */     
/* 280 */     while (m.find()) {
/* 281 */       start += Integer.parseInt(m.group());
/* 282 */       width = Integer.parseInt(m.group());
/*     */     }
/* 284 */     result.add(Integer.valueOf(start - width));
/* 285 */     result.add(Integer.valueOf(width));
/*     */     
/* 287 */     return result;
/*     */   }
/*     */   
/*     */   private boolean checkJunctionSpan(Reads read, String[] parsedMD, int span, double pid) {
/* 291 */     boolean junctionSpan = false;
/* 292 */     int shift = Integer.parseInt(parsedMD[1]);
/* 293 */     int unshift = Integer.parseInt(parsedMD[2]);
/* 294 */     int oldJunction = read.getRefJunction();
/* 295 */     String leftMD = "";
/* 296 */     String rightMD = "";
/* 297 */     double leftpid = 0.0D;
/* 298 */     double rightpid = 0.0D;
/* 299 */     read.setJunctionShift(shift);
/* 300 */     read.setHex(shift);
/*     */     
/* 302 */     int position = oldJunction - read.getStart() + shift + 1;
/* 303 */     junctionSpan = (position > read.getStart() + span / 2) && (position + span / 2 < read.getSequence().length() - 2);
/* 304 */     int counter = 0;
/* 305 */     if (junctionSpan) {
/* 306 */       int startpos = position - span / 2;
/* 307 */       int lastpos = position + span / 2;
/*     */       
/* 309 */       read.setJunctionSeq(parsedMD[0].substring(startpos - 1, lastpos + 1));
/* 310 */       for (int i = startpos; i <= lastpos; i++) {
/* 311 */         if (parsedMD[0].charAt(i) != 'M') {
/* 312 */           junctionSpan = false;
/*     */         }
/*     */         
/* 315 */         counter++;
/*     */       }
/* 317 */       StringBuilder parsed = new StringBuilder(parsedMD[0]);
/* 318 */       leftMD = parsedMD[0].substring(0, startpos);
/* 319 */       rightMD = parsedMD[0].substring(lastpos);
/*     */       
/* 321 */       int matchCount = StringUtils.countMatches(leftMD, "M");
/* 322 */       leftpid = matchCount / leftMD.length();
/* 323 */       matchCount = StringUtils.countMatches(rightMD, "M");
/* 324 */       rightpid = matchCount / rightMD.length();
/*     */       
/* 326 */       parsed.insert(startpos - 1, "<");
/* 327 */       parsed.insert(lastpos + 1, ">");
/* 328 */       read.setLeftpid(String.format("%.3f", new Object[] { Double.valueOf(leftpid) }));
/* 329 */       read.setRightpid(String.format("%.3f", new Object[] { Double.valueOf(rightpid) }));
/* 330 */       read.setMdTransformed(parsed.toString() + "|" + counter + "|" + position + "|" + startpos + "|" + lastpos + "|" + String.format("%.3f", new Object[] { Double.valueOf(leftpid) }) + "|" + String.format("%.3f", new Object[] { Double.valueOf(rightpid) }));
/*     */       
/* 332 */       if ((rightpid < pid) || (leftpid < pid)) {
/* 333 */         junctionSpan = false;
/*     */       }
/*     */     }
/* 336 */     return junctionSpan;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, PTES> getPtes()
/*     */   {
/* 344 */     return this.ptes;
/*     */   }
/*     */   
/*     */   public Map<String, List<Reads>> getCanonical() {
/* 348 */     return this.canonical;
/*     */   }
/*     */ }
