# Mbarara Mama-Check AI

An offline, AI-powered maternal risk stratification and clinical decision support system designed for **Mbarara Regional Referral Hospital (MRRH)**.  
This mobile application empowers expectant mothers by interpreting Antenatal Care (ANC) vitals into actionable risk categories without requiring internet access.

---

## 📱 Features
 **Offline AI Inference**: TensorFlow Lite model runs entirely on-device.
 **Risk Stratification**: Classifies maternal risk into **High (RED)**, **Mid (YELLOW)**, and **Low (GREEN)**.
 **Input Validation**: Ensures safe ranges for vitals before analysis.
 **Longitudinal Tracking**: SQLite database stores assessment history.
 **Health Literacy Module**: Provides educational content on danger signs, nutrition, and safe delivery preparation.
 **Share Functionality**: Export triage reports via WhatsApp/SMS for remote health worker communication.



## 🧪 Machine Learning Model
  **Dataset**: Kaggle *Maternal Health Risk Data Set* (Ahmed et al., 2020) + synthetic augmentation (2,000 records).
  **Baseline Model**: Random Forest for feature importance analysis.
  **Deployment Model**: Lightweight 3-layer MLP (Keras/TensorFlow) converted to `.tflite` for mobile compatibility.
  **Performance**: 74% weighted accuracy, high recall for life-threatening cases.



