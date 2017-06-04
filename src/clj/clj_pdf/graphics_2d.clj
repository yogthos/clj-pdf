(ns clj-pdf.graphics-2d
  (:import
    [java.awt Graphics2D]
    [cljpdf.text.pdf DefaultFontMapper PdfWriter]
    cljpdf.text.Rectangle))

(declare g2d-register-fonts)
(def g2d-fonts-registered? (atom nil))
(def default-font-mapper (DefaultFontMapper.))

(defn with-graphics [{:keys [^PdfWriter pdf-writer page-width page-height font-mapper under translate rotate scale] :as meta} f]
  (let [font-mapper (or font-mapper default-font-mapper)
        template    (if under
                      (.getDirectContentUnder pdf-writer)
                      (.getDirectContent pdf-writer))
        g2d         (.createGraphics template page-width page-height font-mapper)]
    (try
      (when (coll? translate)
        (.translate g2d (double (first translate)) (double (second translate))))
      (when (number? translate)
        (.translate g2d (double translate) (double translate)))
      (when (coll? scale)
        (.scale g2d (double (first scale)) (double (second scale))))
      (when (number? scale)
        (.scale g2d (double scale) (double scale)))
      (when rotate
        (.rotate g2d (double rotate)))

      (f g2d)
      (Rectangle. 0.0 0.0) ; choose a better placeholder?
      (finally
        (.dispose g2d)))))

(def common-font-dirs
  ["/Library/Fonts"
   "/System/Library/Fonts"])

;;; Other common font dirs, the boolean indicates whether recursive descent needed
   ;; "c:/windows/fonts", false
   ;; "c:/winnt/fonts", false
   ;; "d:/windows/fonts", false
   ;; "d:/winnt/fonts", false
   ;; "/usr/share/X11/fonts", true
   ;; "/usr/X/lib/X11/fonts", true
   ;; "/usr/openwin/lib/X11/fonts", true
   ;; "/usr/share/fonts", true
   ;; "/usr/X11R6/lib/X11/fonts", true

(defn g2d-register-fonts []
  (doseq [d common-font-dirs]
    (.insertDirectory default-font-mapper d))
  (reset! g2d-fonts-registered? true))
