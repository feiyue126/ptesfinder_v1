/*     */ package bio.igm.entities;
/*     */ 
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Reads
/*     */ {
/*     */   String id;
/*     */   int orientation;
/*     */   String target;
/*     */   String targetRaw;
/*     */   String locus;
/*     */   int start;
/*     */   String cigar;
/*     */   String mdfield;
/*     */   String editDistance;
/*     */   String sequence;
/*     */   String quality;
/*     */   int refJunction;
/*     */   boolean spansJunction;
/*     */   String junctionSeq;
/*     */   String hex;
/*     */   String line;
/*     */   int junctionShift;
/*  30 */   int aligned = 0;
/*     */   String mdTransformed;
/*  32 */   boolean genomicMatch = false;
/*     */   
/*     */   String genAlignment;
/*     */   
/*     */   String leftpid;
/*     */   
/*     */   String rightpid;
/*     */   
/*     */   public Reads(String line)
/*     */   {
/*  42 */     this.line = line;
/*  43 */     setAttributes(line);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setAttributes(String line)
/*     */   {
/*  51 */     String[] attributes = line.split("\t");
/*  52 */     setId(attributes[0]);
/*  53 */     setOrientation(Integer.parseInt(attributes[1]));
/*  54 */     setTargetRaw(attributes[2]);
/*  55 */     if ((attributes[2].contains(".")) && (!attributes[2].startsWith("gi"))) {
/*  56 */       setTarget(attributes[2].split("\\.")[0] + "." + attributes[2].split("\\.")[1] + "." + attributes[2].split("\\.")[2]);
/*  57 */       setLocus(attributes[2].split("\\.")[0]);
/*  58 */     } else if (attributes[2].startsWith("gi")) {
/*  59 */       setTarget(processID(attributes[2]));
/*  60 */       setLocus(processID(attributes[2]));
/*     */     } else {
/*  62 */       setTarget(attributes[2]);
/*     */     }
/*     */     
/*  65 */     setStart(Integer.parseInt(attributes[3]));
/*  66 */     setCigar(attributes[5]);
/*  67 */     setMdfield(attributes[(attributes.length - 2)]);
/*  68 */     setAligned(attributes[(attributes.length - 2)]);
/*  69 */     setEditDistance(attributes[(attributes.length - 3)]);
/*  70 */     setQuality(attributes[10]);
/*  71 */     if ((attributes[2].contains(".")) && (!attributes[2].startsWith("gi"))) {
/*  72 */       setRefJunction(Integer.parseInt(attributes[2].split("\\.")[3]));
/*  73 */       setSequence(attributes[9]);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String processID(String string) {
/*  78 */     String id = "";
/*  79 */     String[] target = string.split("ref");
/*  80 */     id = target[1].replaceAll("\\..*|[^a-zA-Z0-9_]", "");
/*  81 */     return id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  89 */     if (obj == null) {
/*  90 */       return false;
/*     */     }
/*  92 */     if (getClass() != obj.getClass()) {
/*  93 */       return false;
/*     */     }
/*  95 */     Reads other = (Reads)obj;
/*  96 */     if (this.target == null ? other.target != null : !this.target.equals(other.target)) {
/*  97 */       return false;
/*     */     }
/*  99 */     if (this.sequence == null ? other.sequence != null : !this.sequence.equals(other.sequence)) {
/* 100 */       return false;
/*     */     }
/* 102 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 107 */     int hash = 3;
/* 108 */     hash = 59 * hash + (this.target != null ? this.target.hashCode() : 0);
/* 109 */     return hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCigar()
/*     */   {
/* 119 */     return this.cigar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCigar(String cigar)
/*     */   {
/* 127 */     this.cigar = cigar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getEditDistance()
/*     */   {
/* 135 */     return this.editDistance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEditDistance(String editDistance)
/*     */   {
/* 143 */     this.editDistance = editDistance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getId()
/*     */   {
/* 151 */     return this.id;
/*     */   }
/*     */   
/*     */   private void setId(String id) {
/* 155 */     this.id = id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMdfield()
/*     */   {
/* 163 */     return this.mdfield;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMdfield(String mdfield)
/*     */   {
/* 171 */     this.mdfield = mdfield;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getOrientation()
/*     */   {
/* 179 */     return this.orientation;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOrientation(int orientation)
/*     */   {
/* 187 */     this.orientation = orientation;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getQuality()
/*     */   {
/* 195 */     return this.quality;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setQuality(String quality)
/*     */   {
/* 203 */     this.quality = quality;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getSequence()
/*     */   {
/* 211 */     return this.sequence;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSequence(String sequence)
/*     */   {
/* 219 */     this.sequence = sequence;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getStart()
/*     */   {
/* 227 */     return this.start;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStart(int start)
/*     */   {
/* 235 */     this.start = start;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLocus()
/*     */   {
/* 243 */     return this.locus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLocus(String locus)
/*     */   {
/* 251 */     this.locus = locus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTarget()
/*     */   {
/* 259 */     return this.target;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTarget(String target)
/*     */   {
/* 267 */     this.target = target;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getJunctionSeq()
/*     */   {
/* 275 */     return this.junctionSeq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setJunctionSeq(String junctionSeq)
/*     */   {
/* 283 */     this.junctionSeq = junctionSeq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRefJunction()
/*     */   {
/* 291 */     return this.refJunction;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRefJunction(int refJunction)
/*     */   {
/* 299 */     this.refJunction = refJunction;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSpansJunction()
/*     */   {
/* 307 */     return this.spansJunction;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSpansJunction(boolean spansJunction)
/*     */   {
/* 315 */     this.spansJunction = spansJunction;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLine()
/*     */   {
/* 323 */     return this.line;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getJunctionShift()
/*     */   {
/* 331 */     return this.junctionShift;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setJunctionShift(int junctionShift)
/*     */   {
/* 339 */     this.junctionShift = junctionShift;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTargetRaw()
/*     */   {
/* 347 */     return this.targetRaw;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTargetRaw(String targetRaw)
/*     */   {
/* 355 */     this.targetRaw = targetRaw;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHex()
/*     */   {
/* 363 */     return this.hex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHex(String hex)
/*     */   {
/* 371 */     int pos = getRefJunction() > getStart() + 3 ? getRefJunction() - getStart() + 1 + getJunctionShift() : getRefJunction() + getJunctionShift();
/* 372 */     this.hex = hex.substring(pos - 3, pos + 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setHex(int shift)
/*     */   {
/*     */     try
/*     */     {
/* 381 */       int pos = getRefJunction() > getStart() + 3 ? getRefJunction() - getStart() + 1 + getJunctionShift() : getRefJunction() + getJunctionShift();
/* 382 */       this.hex = getSequence().substring(pos - 3, pos + 3);
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAligned()
/*     */   {
/* 392 */     return this.aligned;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAligned(String aligned)
/*     */   {
/* 401 */     String pattern = "[0-9]+";
/*     */     
/* 403 */     Pattern p = Pattern.compile(pattern);
/* 404 */     Matcher m = p.matcher(aligned.split(":")[2]);
/*     */     
/* 406 */     while (m.find()) {
/* 407 */       this.aligned += Integer.parseInt(m.group());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMdTransformed()
/*     */   {
/* 416 */     return this.mdTransformed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMdTransformed(String mdTransformed)
/*     */   {
/* 424 */     this.mdTransformed = mdTransformed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGenomicMatch()
/*     */   {
/* 432 */     return this.genomicMatch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setGenomicMatch(boolean genomicMatch)
/*     */   {
/* 440 */     this.genomicMatch = genomicMatch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getGenAlignment()
/*     */   {
/* 448 */     return this.genAlignment;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setGenAlignment(String genAlignment)
/*     */   {
/* 456 */     this.genAlignment = genAlignment;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLeftpid()
/*     */   {
/* 464 */     return this.leftpid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLeftpid(String leftpid)
/*     */   {
/* 472 */     this.leftpid = leftpid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRightpid()
/*     */   {
/* 480 */     return this.rightpid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRightpid(String rightpid)
/*     */   {
/* 488 */     this.rightpid = rightpid;
/*     */   }
/*     */ }


 */