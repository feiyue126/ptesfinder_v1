/*     */ package bio.igm.entities;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PTES
/*     */ {
/*  16 */   Map<String, Reads> reads = new HashMap();
/*     */   String id;
/*     */   String locus;
/*     */   int prime5;
/*     */   int prime3;
/*     */   int count;
/*     */   int adj5;
/*     */   int adj3;
/*     */   int totalCanonicalCount;
/*     */   String line;
/*     */   boolean confirmed;
/*     */   boolean spanned;
/*     */   boolean lastExon;
/*     */   
/*     */   public PTES() {}
/*     */   
/*     */   public PTES(String id, int count)
/*     */   {
/*  34 */     setId(id);
/*  35 */     setLocus(id.split("\\.")[0]);
/*  36 */     setPrime5(Integer.parseInt(id.split("\\.")[1]));
/*  37 */     setPrime3(Integer.parseInt(id.split("\\.")[2]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PTES(String line)
/*     */   {
/*  45 */     this.line = line;
/*  46 */     setAttributes(line);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setAttributes(String line)
/*     */   {
/*  53 */     String[] attributes = line.split("\t");
/*  54 */     setId(attributes[0] + "." + attributes[1] + "." + attributes[2]);
/*  55 */     setLocus(attributes[0]);
/*  56 */     setPrime5(Integer.parseInt(attributes[1]));
/*  57 */     setPrime3(Integer.parseInt(attributes[2]));
/*  58 */     setAdj5(Integer.parseInt(attributes[4]));
/*  59 */     setAdj3(Integer.parseInt(attributes[6]));
/*  60 */     setTotalCanonicalCount(Integer.parseInt(attributes[5]) + Integer.parseInt(attributes[7]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRead(Reads read)
/*     */   {
/*  68 */     if (read.getTarget().equalsIgnoreCase(this.id)) {
/*  69 */       this.reads.put(read.getId(), read);
/*  70 */       this.count += 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAdj3()
/*     */   {
/*  79 */     return this.adj3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdj3(int adj3)
/*     */   {
/*  87 */     this.adj3 = adj3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAdj5()
/*     */   {
/*  95 */     return this.adj5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdj5(int adj5)
/*     */   {
/* 103 */     this.adj5 = adj5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCount()
/*     */   {
/* 111 */     return this.count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCount(int count)
/*     */   {
/* 119 */     this.count = count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getId()
/*     */   {
/* 127 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 135 */     this.id = id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLine()
/*     */   {
/* 143 */     return this.line;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLine(String line)
/*     */   {
/* 151 */     this.line = line;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLocus()
/*     */   {
/* 159 */     return this.locus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLocus(String locus)
/*     */   {
/* 167 */     this.locus = locus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPrime3()
/*     */   {
/* 175 */     return this.prime3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPrime3(int prime3)
/*     */   {
/* 183 */     this.prime3 = prime3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPrime5()
/*     */   {
/* 191 */     return this.prime5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPrime5(int prime5)
/*     */   {
/* 199 */     this.prime5 = prime5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Reads> getReads()
/*     */   {
/* 207 */     return this.reads;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReads(Map<String, Reads> reads)
/*     */   {
/* 215 */     this.reads = reads;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTotalCanonicalCount()
/*     */   {
/* 223 */     return this.totalCanonicalCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTotalCanonicalCount(int totalCanonicalCount)
/*     */   {
/* 231 */     this.totalCanonicalCount = totalCanonicalCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isConfirmed()
/*     */   {
/* 240 */     return this.confirmed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setConfirmed(boolean confirmed)
/*     */   {
/* 248 */     this.confirmed = confirmed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSpanned()
/*     */   {
/* 257 */     return this.spanned;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSpanned(boolean spanned)
/*     */   {
/* 265 */     this.spanned = spanned;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLastExon()
/*     */   {
/* 273 */     return this.lastExon;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLastExon(boolean lastExon)
/*     */   {
/* 281 */     this.lastExon = lastExon;
/*     */   }
/*     */ }
